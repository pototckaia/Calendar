package com.example.calendar.permission

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.NoContent
import com.example.calendar.repository.server.NotFind
import com.example.calendar.repository.server.model.EntityType
import com.example.calendar.repository.server.model.PermissionAction
import com.example.calendar.repository.server.model.PermissionRequest
import ru.terrakok.cicerone.Router


@InjectViewState
class PermissionEventPresenter(
    val event_id: Long?,
    val pattern_ids: List<Long>?,
    private val eventRepository: EventRepository,
    private val router: Router
) : BaseMvpSubscribe<PermissionView>() {

    val allEntity = event_id == null


    private fun getPermissions(actions: List<PermissionAction>): List<PermissionRequest> {
        val permission = ArrayList<PermissionRequest>()
        actions.forEach {
            permission.add(PermissionRequest(it, event_id, EntityType.EVENT))
            if (pattern_ids != null) {
                pattern_ids.forEach { pattern_id ->
                    permission.add(PermissionRequest(it, pattern_id, EntityType.PATTERN))
                }
            } else {
                permission.add(PermissionRequest(it, null, EntityType.PATTERN))
            }
        }
        return permission
    }

    fun getLink(actions: List<PermissionAction>) {
        val u = eventRepository.getToken(getPermissions(actions))
            .subscribe({
                viewState.addToClipboard(getTokenFromLink(it))
            }, {
                viewState.showToast(it.toString())
            })
        unsubscribeOnDestroy(u)
    }

    fun getPermission(email: String, actions: List<PermissionAction>) {
        if (event_id == null) {
            return
        }

        val u = eventRepository.getUser(null, email)
            .subscribe({
                val u = eventRepository.getPermission(it.id, getPermissions(actions))
                    .subscribe(
                        {
                            router.exit()
                        },
                        {
                            if (it is NoContent) {
                                // permission exit
                                router.exit()
                            }
                            viewState.showToast(it.toString())
                        })
                unsubscribeOnDestroy(u)

            }, {
                if (it is NotFind || it is InternalError) {
                    viewState.showEmailError()
                    return@subscribe
                }
                viewState.showToast(it.toString())
            })
        unsubscribeOnDestroy(u)

    }
}

fun getTokenFromLink(link: String) = link.split("/".toRegex()).last()
