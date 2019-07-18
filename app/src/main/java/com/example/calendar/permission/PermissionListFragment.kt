package com.example.calendar.permission

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.customView.PermissionRecycleViewAdapter
import com.example.calendar.customView.ProgressBarDialog
import com.example.calendar.inject.InjectApplication
import com.example.calendar.repository.server.model.PermissionModel
import kotlinx.android.synthetic.main.fragment_event.view.*
import kotlinx.android.synthetic.main.fragment_month_calendar.view.*
import kotlinx.android.synthetic.main.fragment_permission_list.view.*


class PermissionListFragment : MvpAppCompatFragment(), PermissionListView {

    companion object {
        fun newInstance() = PermissionListFragment()
    }

    @InjectPresenter
    lateinit var permissionListPresenter: PermissionEventListPresenter

    @ProvidePresenter
    fun providePermissionListPresenter(): PermissionEventListPresenter {
        return PermissionEventListPresenter(
            InjectApplication.inject.repository
        )
    }


    lateinit var v: View
    var selectedButtonColor = -1
    var noSelectedButtonColor = -1
    lateinit var progressDialog: ProgressBarDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_permission_list,
            container, false
        )
        initToolBar()
        progressDialog = ProgressBarDialog(context!!)
        val linerLayoutManager = LinearLayoutManager(context)
        val adapter = PermissionRecycleViewAdapter(ArrayList<PermissionModel>(), this::onPermissionDelete)
        val dividerItemDecoration = DividerItemDecoration(v.rvPermission.context, linerLayoutManager.orientation)
        v.rvPermission.run {
            this.adapter = adapter
            this.layoutManager = linerLayoutManager
            this.addItemDecoration(dividerItemDecoration)
        }

        selectedButtonColor = resources.getColor(R.color.fui_bgPhone)
        noSelectedButtonColor = resources.getColor(android.R.color.holo_blue_bright)
        v.bIUser.setOnClickListener {
            val color = (v.bIUser.background as ColorDrawable).color
            val isChecked = color == selectedButtonColor
            if (!isChecked) {
                v.bIOwner.background = ColorDrawable(noSelectedButtonColor)
                v.bIUser.background = ColorDrawable(selectedButtonColor)
                permissionListPresenter.onMineSwitch(PermissionEventListPresenter.I_USER)
            }
        }

        v.bIOwner.setOnClickListener {
            val isChecked = (v.bIOwner.background as ColorDrawable).color == selectedButtonColor
            if (!isChecked) {
                v.bIUser.setBackgroundColor(noSelectedButtonColor)
                v.bIOwner.setBackgroundColor(selectedButtonColor)
                permissionListPresenter.onMineSwitch(PermissionEventListPresenter.I_OWNER)
            }
        }

        return v
    }

    private fun initToolBar() {
        v.tbPermissionList.setNavigationOnClickListener { InjectApplication.inject.router.exit() }
        v.tbPermissionList.inflateMenu(R.menu.menu_permission_list)
        v.tbPermissionList.menu.findItem(R.id.actionReload).setOnMenuItemClickListener {
            permissionListPresenter.reload()
            true
        }
    }

    private fun onPermissionDelete(p: PermissionModel, pos: Int) {
        permissionListPresenter.onDelete(p, pos)
    }

    override fun remove(pos: Int) {
        (v.rvPermission.adapter as PermissionRecycleViewAdapter).removeByPos(pos)
    }

    override fun showToast(mes: String) {
        Toast.makeText(context!!, mes, Toast.LENGTH_SHORT).show()
    }

    override fun setPermission(mine: Boolean, p: ArrayList<PermissionModel>) {
        if (permissionListPresenter.curMine == mine) {
            (v.rvPermission.adapter as PermissionRecycleViewAdapter).updateAll(p)
        }
    }

    override fun showLoading() {
        progressDialog.show()
    }

    override fun stopLoading() {
        progressDialog.dismiss()
    }
}