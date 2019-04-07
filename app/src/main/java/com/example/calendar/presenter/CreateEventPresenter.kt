package com.example.calendar.presenter

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.presenter.BackPressedPresenter
import com.example.calendar.view.CreateEventInfoView
import io.reactivex.schedulers.Schedulers
import java.util.*

@InjectViewState
class CreateEventPresenter(
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<CreateEventInfoView>() {

    // TODO stop repeat press save
    fun onSaveEvent(
        title: String, startEvent: Calendar, endEvent: Calendar,
        back: BackPressedPresenter
    ) {
        val event = EventTable(name = title, started_at = startEvent, ended_at = endEvent)

        val subscription = eventRepository.insert(event)
            .subscribeOn(Schedulers.io())
            .subscribe() {
                back.onBackPressed()
            }
        unsubscribeOnDestroy(subscription)

    }
}