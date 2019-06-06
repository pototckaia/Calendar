package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventInstance
import com.example.calendar.data.EventRecurrence
import com.example.calendar.data.EventRecurrenceRepository
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
                    onUpdateLoading(newEventInstance)
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    fun onDeleteAll() {
        val sub = eventRepository.deleteAllEvent(eventInstance)
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
        newEventInstance.startedAtNotUpdate = newEventInstance.startedAtInstance
        eventInstance = newEventInstance

        viewState.updateEventInfo(eventInstance)
    }

    private fun onDeleteLoading() {
        router.exit()
    }
}