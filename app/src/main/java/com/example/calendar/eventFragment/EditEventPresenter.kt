package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventInstance
import com.example.calendar.data.EventRecurrence
import com.example.calendar.data.EventRecurrenceRepository
import com.example.calendar.helpers.BaseMvpSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import ru.terrakok.cicerone.Router

@InjectViewState
class EditEventPresenter(
    private val router: Router,
    private val eventRepository: EventRecurrenceRepository,
    private var eventInstance: EventInstance
) : BaseMvpSubscribe<EditEventView>() {


    init {
        loadEvent(eventInstance.idEventRecurrence)
    }

    private fun loadEvent(id: String) {
        val subscription = eventRepository.getEventById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { repositories ->
                    onLoadingSuccess(repositories)
                },
                { error ->
                    onLoadingFailed(error.toString())
                });
        unsubscribeOnDestroy(subscription)
    }

    fun onUpdate(
        title: String,
        note: String,
        startEvent: ZonedDateTime,
        endEvent: ZonedDateTime,
        rule: String
    ) {

        val newEventInstance = EventInstance(
            idEventRecurrence = eventInstance.idEventRecurrence,
            nameEventRecurrence = title,
            noteEventRecurrence = note,
            startedAtInstance = startEvent,
            startedAtNotUpdate = eventInstance.startedAtNotUpdate,
            duration = Duration.between(
                startEvent.withZoneSameInstant(ZoneOffset.UTC),
                endEvent.withZoneSameInstant(ZoneOffset.UTC)),
            rrule = rule
        )

        val sub = eventRepository.updateAllEvent(newEventInstance)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    newEventInstance.startedAtNotUpdate = newEventInstance.startedAtInstance
                    eventInstance = newEventInstance

                    viewState.updateEventInfo(eventInstance)
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    fun onDelete() {
//        val sub = eventRepository.delete(event)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    router.exit()
//                },
//                { error ->
//                    onLoadingFailed(error.toString())
//                })
//        unsubscribeOnDestroy(sub)
    }

    private fun onLoadingFailed(error: String) {
        viewState.showError(error);
    }

    private fun onLoadingSuccess(rep: List<EventRecurrence>) {
        if (rep.isEmpty()) {
            // todo when not exist
        } else {
            viewState.updateEventInfo(eventInstance)
        }
    }

}