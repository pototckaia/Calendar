package com.example.calendar.eventFragment

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router

@InjectViewState
class EditEventInstancePresenter(
    private val router: Router,
    private val eventRepository: EventRepository,
    private var eventInstance: EventInstance
) : BaseMvpSubscribe<EditEventInstanceView>() {

    private var editEventPattern = eventInstance.pattern.patternRequest
    private var isDelete = false

    init {
        viewUpdate(eventInstance)
        loadEvent(eventInstance.entity.id)
    }


    private fun onFirstLoading(rep: Event) {}

    fun onUpdateAll(eventRequest: EventRequest) {
        val newEventInstance = eventInstance
        newEventInstance.entity.eventRequest = eventRequest
        newEventInstance.setStartedAt(editEventPattern.started_at)
        newEventInstance.setEndedAt(editEventPattern.duration)

        val pattern = newEventInstance.pattern.patternRequest
        pattern.setRecurrence(editEventPattern.rrule)
        pattern.timezone = editEventPattern.timezone
        newEventInstance.pattern.patternRequest = pattern

//        if (newEventInstance == eventInstance) {
//            return
//        }

        Log.d("Update__", "update", null)
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

    fun onDeleteAll() {
        if (isDelete) {
            return
        }

        isDelete = true
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

    fun onPatternChange(e: PatternRequest) {
        editEventPattern = e
    }

    private fun viewUpdate(instance: EventInstance) {
        eventInstance = instance
        editEventPattern = instance.pattern.patternRequest
        editEventPattern.setStartedAt(instance.started_at)
        editEventPattern.set_duration(instance.ended_at)

        val eventRequest = eventInstance.entity.eventRequest
        viewState.updateEventInfo(
            eventInstance.entity.owner_id,
            eventRequest,
            editEventPattern)
    }

    private fun onLoadingFailed(error: String) {
        viewState.showError(error);
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

    private fun onUpdateLoading(newEventInstance: EventInstance) {
        viewUpdate(newEventInstance)
    }

    private fun onDeleteLoading() {
        router.exit()
    }
}