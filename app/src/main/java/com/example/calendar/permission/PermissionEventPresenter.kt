package com.example.calendar.permission

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EntityType
import com.example.calendar.repository.server.model.PermissionAction
import com.example.calendar.repository.server.model.PermissionRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.terrakok.cicerone.Router


@InjectViewState
class PermissionEventPresenter(
    val event_id: Long?,
    val pattern_id: Long?,
    private val eventRepository: EventRepository,
    private val router: Router
) : BaseMvpSubscribe<PermissionView>() {

    val allEntity = event_id == null

    fun getLink(permission: List<PermissionAction>) {

        val request = ArrayList<PermissionRequest>()
        permission.forEach {
            request.add(PermissionRequest(
                action = it, entity_id = event_id, entity_type = EntityType.EVENT))
            request.add(PermissionRequest(
                action = it, entity_id = pattern_id, entity_type = EntityType.PATTERN))
        }


        val u = eventRepository.getToken(request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                viewState.addToClipboard(getTokenFromLink(it))
                router.exit()
            }, {
                viewState.showToast(it.toString())
            })
        unsubscribeOnDestroy(u)
    }
}

fun getTokenFromLink(link : String) = link.split("/".toRegex()).last()
