package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.remove.BackPressedPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

@InjectViewState
class EditEventPresenter(private val eventRepository: EventRepository,
                         private val id: String)
    : BaseMvpSubscribe<EditEventView>() {

    private lateinit var event: EventTable

    init {
        loadEvent()
    }

    private fun loadEvent() {
        val subscription = eventRepository.getUserById(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ repositories ->
                onLoadingSuccess(repositories)
            }, { error ->
                // todo when not exist
                onLoadingFailed(error)
            });
        unsubscribeOnDestroy(subscription)
    }

    fun onUpdate(test: String, start: Calendar, end: Calendar) {
        event.run {
            this.name = test
            this.started_at.timeInMillis = start.timeInMillis
            this.ended_at.timeInMillis = end.timeInMillis
        }

        val sub = eventRepository.update(event)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.updateEventInfo(event)
            }, { error->
                onLoadingFailed(error)
            })
        unsubscribeOnDestroy(sub)
     }

    fun onDelete(back: BackPressedPresenter) {
        val sub = eventRepository.delete(event)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    back.onBackPressed()
                }, { error->
                    onLoadingFailed(error)
                })
        unsubscribeOnDestroy(sub)
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