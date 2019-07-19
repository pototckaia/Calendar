package com.example.calendar.permission

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EntityType
import com.example.calendar.repository.server.model.PermissionAction
import com.example.calendar.repository.server.model.PermissionModel


@InjectViewState
class PermissionEventListPresenter(
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<PermissionListView>() {
    var iOwnerPermission = ArrayList<PermissionModel>()
    var iUserPermission = ArrayList<PermissionModel>()
    var curMine: Boolean? = null


    init {
        // мне дали доступ
        loadPermission(I_OWNER, iOwnerPermission) { }
        // я дал доступ - true
        loadPermission(I_USER, iUserPermission) {

            onMineSwitch(I_USER)
        }
    }

    private fun getPermission(m: Boolean) = if (m == I_USER) iUserPermission else iOwnerPermission

    private fun loadPermission(mine: Boolean, outPermissions: ArrayList<PermissionModel>, onSuccess: () -> Unit) {
        viewState.showLoading()
        val u = eventRepository.getEventPermissions(mine, "Все события", "Имя не задано")
            .subscribe({
                outPermissions.clear()
                outPermissions.addAll(it)
                onSuccess()
                viewState.stopLoading()
            }, {
                viewState.showToast(it.toString())
                viewState.stopLoading()
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
        }
        loadPermission(!curMine, getPermission(!curMine)) {}
    }

    fun onDelete(p: PermissionModel, pos: Int) {
        if (curMine != p.mine) {
            return
        }

        val permissions = getPermission(curMine!!)

        val update = ArrayList<Pair<Int, PermissionModel>>()
        update.add(Pair(pos, p))
        if (p.actionType == PermissionAction.READ) {
            val l = permissions
                .withIndex()
                .filter { (i, it) ->
                    it.entity_id == p.entity_id &&
                            (it.actionType == PermissionAction.UPDATE || it.actionType == PermissionAction.DELETE)

                }.map { (i, value) -> Pair(i, value) }
            update.addAll(l)
        }

        val u = eventRepository.revokeEventPermission(update.map { it.second })
            .subscribe({
                update.forEach {
                    val p = it.first
                    viewState.remove(pos)
                    permissions.removeAt(pos)
                }
            }, {
                viewState.showToast(it.toString())
            })


    }

    companion object {
        val I_OWNER = false
        val I_USER = true
    }
}