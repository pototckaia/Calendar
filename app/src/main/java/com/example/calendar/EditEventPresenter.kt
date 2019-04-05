package com.example.calendar

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.view.EditEventView
import io.reactivex.android.schedulers.AndroidSchedulers

@InjectViewState
class EditEventPresenter(private val eventRepository: EventRepository,
                         private val id: String)
    : BaseMvpSubscribe<EditEventView>() {

    private lateinit var event: EventTable

    init {
        loadEvent()
    }

    fun loadEvent() {
        val subscription = eventRepository.getUserById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ repositories ->
                onLoadingSuccess(repositories)
            }, { error ->
                onLoadingFailed(error)
            });
        unsubscribeOnDestroy(subscription)
    }

    private fun onLoadingFailed(error: Throwable) {
        viewState.showError(error.toString());
    }

    private fun onLoadingSuccess(rep: List<EventTable>) {
        if (rep.isEmpty()) {

        } else {
            event = rep.first()
            viewState.updateEventInfo(event)
        }
    }

}