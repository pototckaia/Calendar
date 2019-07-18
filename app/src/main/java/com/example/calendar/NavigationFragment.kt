package com.example.calendar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.calendarFragment.WeekCalendarFragment.TypeView
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_navigation.view.*
import android.content.Intent
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.widget.LinearLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.customView.ProgressBarDialog
import com.example.calendar.export.ExportImportPresenter
import com.example.calendar.export.FileUtils
import com.example.calendar.export.LoadingView
import com.example.calendar.permission.ActivateTokenPresenter
import com.example.calendar.permission.ActivateTokenView
import kotlinx.android.synthetic.main.dialog_acivate_token.view.*
import java.io.File


class NavigationFragment :
    MvpAppCompatFragment(),
    LoadingView, ActivateTokenView {


    companion object {
        val EXPORT_PERMISION = 12
        val IMPORT_PERMISSION = 13
        val OPEN_FILE = 14
        val CREATE_FILE = 15

        fun newInstance() : NavigationFragment {
            return NavigationFragment()
        }
    }

    val router = InjectApplication.inject.router
    val repository = InjectApplication.inject.repository

    @InjectPresenter
    lateinit var exportImportPresenter: ExportImportPresenter

    @ProvidePresenter
    fun provideExportImportPresenter(): ExportImportPresenter {
        return ExportImportPresenter(
            repository,
            InjectApplication.inject.contentResolver
        )
    }

    @InjectPresenter
    lateinit var activateTokenPresenter: ActivateTokenPresenter

    @ProvidePresenter
    fun provideActivateTokenPresenter(): ActivateTokenPresenter {
        return ActivateTokenPresenter(repository)
    }

    lateinit var progressDialog: ProgressBarDialog
    lateinit var activateTokenDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(
            R.layout.fragment_navigation,
            container, false
        )
        progressDialog = ProgressBarDialog(context!!)

        v.bMonth.setOnClickListener {
            router.navigateTo(Screens.MonthCalendarScreen())
        }

        v.bWeek.setOnClickListener {
            router.navigateTo(Screens.WeekCalendarScreen(TypeView.WEEK))
        }

        v.bDay.setOnClickListener {
            router.navigateTo(Screens.WeekCalendarScreen(TypeView.DAY))
        }

        v.bSignOut.setOnClickListener {
            AuthUI.getInstance()
                .signOut(context!!)
                .addOnCompleteListener {
                    router.navigateTo(Screens.AuthScreen())
                }

        }

        v.bExport.setOnClickListener { onExport() }
        v.bImport.setOnClickListener { onImport() }

        v.bActivateToken.setOnClickListener { activateTokenPresenter.onActivateClick() }

        v.bCreatePermissionAll.setOnClickListener {
//            router.navigateTo(Screens.CreateEventPermissionScreen())
        }

        return v
    }

    override fun showDialog() {
        val v = View.inflate(context, R.layout.dialog_acivate_token, null)
        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder
            .setView(v)
            .setCancelable(true)
            .setPositiveButton(context!!.getString(android.R.string.ok), null)

        activateTokenDialog = alertBuilder.create()
        activateTokenDialog.setOnShowListener {
                val button = activateTokenDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.setOnClickListener {
                    activateTokenPresenter.activate(v.etToken.text.toString())
                }
            }
        activateTokenDialog.show()
    }

    override fun dismissDialog() {
        activateTokenDialog.dismiss()
    }

    private fun isPermissionGranted(c: Context, permission: String) =
        ContextCompat.checkSelfPermission(c, permission) == PackageManager.PERMISSION_GRANTED

    private fun checkWriteReadPermissions(requestCode: Int) {
        val write = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (!isPermissionGranted(context!!, write)) {
            requestPermissions(arrayOf(write), requestCode)
            return
        }

        val read = Manifest.permission.READ_EXTERNAL_STORAGE
        if (!isPermissionGranted(context!!, read)) {
            requestPermissions(arrayOf(read), requestCode)
            return
        }
    }

    private fun onImport() {
        checkWriteReadPermissions(IMPORT_PERMISSION)
        showFile("*/.ics")
    }

    private fun onExport() {
        checkWriteReadPermissions(EXPORT_PERMISION)
        createFile("*/.ics", "Calendar.ics")
    }

    private fun onImport(uri: Uri) {
        val path = FileUtils.getPath(activity!!, uri)
        exportImportPresenter.onImport(File(path))
    }

    private fun onExport(uri: Uri) {
        exportImportPresenter.onExport(uri)
    }

    private fun showFile(type: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            this.type = type
        }
        startActivityForResult(intent, OPEN_FILE)
    }

    private fun createFile(type: String, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            this.type = type
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    override fun showLoading() {
        progressDialog.show()
    }

    override fun stopLoading() {
        progressDialog.dismiss()
    }

    override fun showToast(mes: String) {
        Toast.makeText(context!!, mes, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        resultData: Intent?
    ) {
        if (resultData == null) {
            return
        }

        when (requestCode) {
            OPEN_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = resultData.data!!
                    onImport(uri)
                }
            }
            CREATE_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = resultData.data!!
                    onExport(uri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (i in 0 until permissions.size) {
            val grantResult = grantResults[i]
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return
            }
        }

        when (requestCode) {
            EXPORT_PERMISION -> { onExport() }
            IMPORT_PERMISSION -> { onImport() }
        }
    }
}