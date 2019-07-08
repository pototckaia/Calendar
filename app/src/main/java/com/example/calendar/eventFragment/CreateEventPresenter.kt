package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EventRequest
import com.example.calendar.repository.server.model.PatternRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import ru.terrakok.cicerone.Router


@InjectViewState
class CreateEventPresenter(
    private val router: Router,
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<CreateEventInfoView>() {

    fun onSaveEvent(
        event: EventRequest,
        pattern: PatternRequest
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