package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.oldEvent.EventRepository
import com.example.calendar.data.oldEvent.EventTable
import com.example.calendar.helpers.BaseMvpSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import java.util.*

@InjectViewState
class CreateEventPresenter(
    private val router: Router,
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<CreateEventInfoView>() {

    fun onSaveEvent(
        title: String, startEvent: Calendar, endEvent: Calendar
    ) {
        val event =
            EventTable(name = title, started_at = startEvent, ended_at = endEvent)

        val subscription = eventRepository.insert(event)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                router.exit()
            }
        unsubscribeOnDestroy(subscription)
    }
}