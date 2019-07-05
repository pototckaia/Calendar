package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.repository.db.EventInstance
import com.example.calendar.repository.db.EventRecurrence
import com.example.calendar.repository.db.EventRecurrenceRepository
import com.example.calendar.helpers.BaseMvpSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
                    onFirstLoading(repositories)
                },
                { error ->
                    onLoadingFailed(error.toString())
                });
        unsubscribeOnDestroy(subscription)
    }

    fun onUpdateAll(
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
            startedAtNotUpdate = eventInstance.startedAtLocalNotUpdate,
            zoneId = eventInstance.zoneId,
            duration = Duration.between(
                startEvent.withZoneSameInstant(ZoneOffset.UTC),
                endEvent.withZoneSameInstant(ZoneOffset.UTC)
            ),
            rrule = rule
        )

        val sub = eventRepository.updateAll(newEventInstance)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onUpdateLoading(newEventInstance)
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    fun onUpdateFuture(
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
            startedAtNotUpdate = eventInstance.startedAtLocalNotUpdate,
            zoneId = eventInstance.zoneId,
            duration = Duration.between(
                startEvent.withZoneSameInstant(ZoneOffset.UTC),
                endEvent.withZoneSameInstant(ZoneOffset.UTC)
            ),
            rrule = rule
        )

        val sub = eventRepository.updateFuture(newEventInstance)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onUpdateLoading(newEventInstance)
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    fun onDeleteAll() {
        val sub = eventRepository.deleteAll(eventInstance)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onDeleteLoading()
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    fun onDeleteFuture() {
        val sub = eventRepository.deleteFuture(eventInstance)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onDeleteLoading()
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    private fun onLoadingFailed(error: String) {
        viewState.showError(error);
    }

    private fun onFirstLoading(rep: List<EventRecurrence>) {
        if (rep.isEmpty()) {
            // todo when not exist
        } else {
            viewState.updateEventInfo(eventInstance)
        }
    }

    fun isEventRecurrence() = eventInstance.isRecurrence()

    private fun onUpdateLoading(newEventInstance: EventInstance) {
        newEventInstance.startedAtLocalNotUpdate = newEventInstance.startedAtLocal
        eventInstance = newEventInstance

        viewState.updateEventInfo(eventInstance)
        router.exit()
    }

    private fun onDeleteLoading() {
        router.exit()
    }
}