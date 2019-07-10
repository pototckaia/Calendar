package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.isRecurrence
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.Event
import com.example.calendar.repository.server.model.EventInstance
import com.example.calendar.repository.server.model.EventServer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import ru.terrakok.cicerone.Router

@InjectViewState
class EditEventPresenter(
    private val router: Router,
    private val eventRepository: EventRepository,
    private var eventInstance: EventInstance
) : BaseMvpSubscribe<EditEventView>() {


    init {
        loadEvent(eventInstance.entity.id)
    }

    private fun loadEvent(id: Long) {
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
        // todo save old event ??
        eventInstance.entity.name = title
        eventInstance.entity.details = note
        // todo location and status

        eventInstance.setStartedAt(startEvent)
        eventInstance.setEndedAt(endEvent)
        // todo check it need ?
        eventInstance.pattern.timezone = startEvent.zone
        // todo add recurrence
//        eventInstance.pattern.setRecurrence(rule)

        val sub = eventRepository.updateAll(eventInstance)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onUpdateLoading(eventInstance)
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

//    fun onUpdateFuture(
//        title: String,
//        note: String,
//        startEvent: ZonedDateTime,
//        endEvent: ZonedDateTime,
//        rule: String
//    ) {
//
//        val newEventInstance = EventInstance(
//            idEventRecurrence = eventInstance.idEventRecurrence,
//            nameEventRecurrence = title,
//            noteEventRecurrence = note,
//            startedAtInstance = startEvent,
//            startedAtNotUpdate = eventInstance.startedAtLocalNotUpdate,
//            zoneId = eventInstance.zoneId,
//            duration = Duration.between(
//                startEvent.withZoneSameInstant(ZoneOffset.UTC),
//                endEvent.withZoneSameInstant(ZoneOffset.UTC)
//            ),
//            rrule = rule
//        )
//
//        val sub = eventRepository.updateFuture(newEventInstance)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    onUpdateLoading(newEventInstance)
//                },
//                { error ->
//                    onLoadingFailed(error.toString())
//                })
//        unsubscribeOnDestroy(sub)
//    }

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

//    fun onDeleteFuture() {
//        val sub = eventRepository.deleteFuture(eventInstance)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    onDeleteLoading()
//                },
//                { error ->
//                    onLoadingFailed(error.toString())
//                })
//        unsubscribeOnDestroy(sub)
//    }

    private fun onLoadingFailed(error: String) {
        viewState.showError(error);
    }

    // todo why need first loading
    private fun onFirstLoading(rep: Event) {
//        if (rep.isEmpty()) {
            // todo when not exist
//        } else {
            viewState.updateEventInfo(eventInstance)
//        }
    }

    fun isEventRecurrence() = isRecurrence(eventInstance.pattern.rrule)

    private fun onUpdateLoading(newEventInstance: EventInstance) {
//        eventInstance = newEventInstance

        viewState.updateEventInfo(eventInstance)
        router.exit()
    }

    private fun onDeleteLoading() {
        router.exit()
    }
}