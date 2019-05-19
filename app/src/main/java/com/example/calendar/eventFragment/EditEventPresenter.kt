package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventInstance
import com.example.calendar.data.EventRecurrence
import com.example.calendar.data.EventRecurrenceRepository
import com.example.calendar.helpers.BaseMvpSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZonedDateTime
import ru.terrakok.cicerone.Router

@InjectViewState
class EditEventPresenter(
    private val router: Router,
    private val eventRepository: EventRecurrenceRepository,
    private val eventInstance: EventInstance
) : BaseMvpSubscribe<EditEventView>() {


    init {
        loadEvent()
    }

    private fun loadEvent() {
//        val subscription = eventRepository.getUserById(id)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { repositories ->
//                    onLoadingSuccess(repositories)
//                },
//                { error ->
//                    // todo when not exist
//                    onLoadingFailed(error.toString())
//                });
//        unsubscribeOnDestroy(subscription)
    }

    fun onUpdate(
        title: String,
        note: String,
        startEvent: ZonedDateTime,
        endEvent: ZonedDateTime,
        rule: RecurrenceRule
    ) {
//        event.run {
//            this.name = test
//            this.started_at.timeInMillis = start.timeInMillis
//            this.ended_at.timeInMillis = end.timeInMillis
//        }
//
//        val sub = eventRepository.update(event)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    viewState.updateEventInfo(event)
//                },
//                { error ->
//                    onLoadingFailed(error.toString())
//                })
//        unsubscribeOnDestroy(sub)
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

        } else {
//            event = rep.first()
//            viewState.updateEventInfo(event)
        }
    }

}