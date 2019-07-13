package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EventRequest
import com.example.calendar.repository.server.model.PatternRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router


@InjectViewState
class CreateEventPresenter(
    private val router: Router,
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<CreateEventView>() {

    fun onSaveEvent(
        event: EventRequest,
        pattern: ArrayList<PatternRequest>
    ) {

        val subscription = eventRepository.insertEvent(event, pattern)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                router.exit()
            }

        unsubscribeOnDestroy(subscription)
    }
}