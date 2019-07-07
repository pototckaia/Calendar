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
        title: String,
        note: String,
        start: ZonedDateTime,
        end: ZonedDateTime,
        rule: String
    ) {
        // todo recurrence
        val event =
            EventRequest(
                name = title,
                details = note,
                location = "",
                status = ""
            )

        // todo list pattern
        val pattern = PatternRequest(
            // todo add in constructor UTC timezone
            started_at = start.withZoneSameInstant(ZoneOffset.UTC),
            ended_at = end.withZoneSameInstant(ZoneOffset.UTC),
            // todo check duration
            duration = Duration.between(start, end),
            timezone = start.zone,
            exrules = emptyList(),
            rrule = ""
        )

        val subscription = eventRepository.insertEvent(event, pattern)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                router.exit()
            }

        unsubscribeOnDestroy(subscription)
    }
}