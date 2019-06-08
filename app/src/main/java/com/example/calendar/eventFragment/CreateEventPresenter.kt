package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventRecurrence
import com.example.calendar.data.EventRecurrenceRepository
import com.example.calendar.helpers.BaseMvpSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import ru.terrakok.cicerone.Router


@InjectViewState
class CreateEventPresenter(
    private val router: Router,
    private val eventRepository: EventRecurrenceRepository
) : BaseMvpSubscribe<CreateEventInfoView>() {

    fun onSaveEvent(
        title: String,
        note: String,
        start: ZonedDateTime,
        end: ZonedDateTime,
        rule: String
    ) {
        val event =
            EventRecurrence(title, note,
                start, Duration.between(start, end), rule)

        val subscription = eventRepository.insertEvent(event)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                router.exit()
            }
        unsubscribeOnDestroy(subscription)
    }
}