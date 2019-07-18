package com.example.calendar.permission

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.PermissionModel


@InjectViewState
class PermissionEventListPresenter(
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<PermissionListView>() {
    var iOwnerPermission = ArrayList<PermissionModel>()
    var iUserPermission = ArrayList<PermissionModel>()
    var curMine : Boolean? = null


    init {
        // мне дали доступ
        viewState.showLoading()
        loadPermission(I_OWNER, iOwnerPermission) { viewState.stopLoading() }
        // я дал доступ - true
        loadPermission(I_USER, iUserPermission) {  viewState.stopLoading() }
    }

    private fun getPermission(m: Boolean) = if (m == I_USER) iUserPermission else iOwnerPermission

    private fun loadPermission(mine: Boolean, outPermissions: ArrayList<PermissionModel>, onSuccess: () -> Unit) {
        val u = eventRepository.getEventPermissions(mine, "Все события", "Имя не задано")
            .subscribe({
                outPermissions.clear()
                outPermissions.addAll(it)
                onSuccess()
            }, {
                viewState.showToast(it.toString())
            })
        unsubscribeOnDestroy(u)
    }

    fun onMineSwitch(mine: Boolean) {
        curMine = mine
        viewState.setPermission(mine, getPermission(mine))
    }

    fun reload() {
        if (curMine == null) {
            return
        }
        val curMine = curMine!!
        val p = getPermission(curMine)
        viewState.showLoading()
        loadPermission(curMine, p) {
            viewState.setPermission(curMine, p)
            viewState.stopLoading()
        }
        loadPermission(!curMine, getPermission(!curMine)) {}
    }

    fun onDelete(p: PermissionModel, pos: Int) {
        if (curMine == p.mine) {
            // todo server
            getPermission(curMine!!).removeAt(pos)
            viewState.remove(pos)
        }
    }

    companion object {
        val I_OWNER = false
        val I_USER = true
    }
}