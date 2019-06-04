package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.withYearMonthDay
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import ru.terrakok.cicerone.Router

@InjectViewState
class FreqPresenter(
    ruleString: String,
    start_: ZonedDateTime) :
    BaseMvpSubscribe<RecurrenceRuleView>() {

    private val start = start_.withZoneSameInstant(ZoneId.systemDefault())

    var untilUTC = ZonedDateTime.now(ZoneId.systemDefault())
    val until : ZonedDateTime
        get() = untilUTC.withZoneSameInstant(ZoneId.systemDefault())

    var isNotRule : Boolean = ruleString.isEmpty()

    init {
        if (isNotRule) {
            viewState.setViewNotRule()
            viewState.setUntil(start)
        } else {
            val rule = RecurrenceRule(ruleString)
            if (rule.until != null) {
                untilUTC = fromDateTimeUTC(rule.until)
                viewState.setUntil(until)
            } else {
                viewState.setUntil(start)
            }
            viewState.setViewRule(rule)
        }
    }

    fun onUntilClick() {
        val l = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val until_ = withYearMonthDay(until, year, monthOfYear, dayOfMonth)
                .truncatedTo(ChronoUnit.DAYS)
                .withZoneSameInstant(ZoneId.systemDefault())
            if (until_ < start) {
                untilUTC = start.withZoneSameInstant(ZoneOffset.UTC)
                // todo hard
                viewState.showToast("Дата оконачания должна быть позже даты начала")
            } else {
                untilUTC = until_.withZoneSameInstant(ZoneOffset.UTC)
            }
            viewState.setUntil(until)
        }
        viewState.openCalendar(start, l)

    }

    fun onSelectItemFreq(pos: Int) {
        isNotRule = pos == FreqView.NEVER.pos
    }

    fun onBack() {
        if (isNotRule) {
            viewState.onExit("")
        } else {
            val r = RecurrenceRule("FREQ=DAILY;")
            viewState.onSave(r)
            viewState.onExit(r.toString())
        }
    }
}
