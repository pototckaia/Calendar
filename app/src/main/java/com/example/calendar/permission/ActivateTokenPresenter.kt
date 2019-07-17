package com.example.calendar.permission

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.NetworkException
import io.reactivex.android.schedulers.AndroidSchedulers


@InjectViewState
class ActivateTokenPresenter(
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<ActivateTokenView>() {

    fun onActivateClick() {
        viewState.showDialog()
    }

    fun activate(token: String) {
        val u = eventRepository.activateToken(token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                viewState.dismissDialog()
            }, {
                var mes = it.toString()
                if (it is NetworkException) {
                    mes ="Token not valid"
                }
                viewState.showToast(mes)
            })
        unsubscribeOnDestroy(u)
    }
}
