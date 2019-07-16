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
import android.net.Uri
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.customView.ProgressBarDialog
import com.example.calendar.impor.ExportImportPresenter
import com.example.calendar.impor.LoadingView


class NavigationFragment :
    MvpAppCompatFragment(),
    LoadingView {


    companion object {
        val EXPORT_PERMISION = 12
        val IMPORT_PERMISSION = 13
        val OPEN_FILE = 14
        val CREATE_FILE = 15

        fun newInstance() : NavigationFragment {
            return NavigationFragment()
        }
    }

    @InjectPresenter
    lateinit var exportImportPresenter: ExportImportPresenter

    @ProvidePresenter
    fun provideExportImportPresenter(): ExportImportPresenter {
        return ExportImportPresenter(
            InjectApplication.inject.repository,
            InjectApplication.inject.contentResolver
        )
    }

    lateinit var progressDialog: ProgressBarDialog

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
            InjectApplication.inject.router.navigateTo(Screens.MonthCalendarScreen())
        }

        v.bWeek.setOnClickListener {
            InjectApplication.inject.router.navigateTo(
                Screens.WeekCalendarScreen(TypeView.WEEK))
        }

        v.bDay.setOnClickListener {
            InjectApplication.inject.router.navigateTo(
                Screens.WeekCalendarScreen(TypeView.DAY))
        }

        v.bSignOut.setOnClickListener {
            AuthUI.getInstance()
                .signOut(context!!)
                .addOnCompleteListener {
                    InjectApplication.inject.router.navigateTo(Screens.AuthScreen())
                }

        }

        v.bExport.setOnClickListener { onExport() }
        v.bImport.setOnClickListener { onImport() }

        return v
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
        // todo
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