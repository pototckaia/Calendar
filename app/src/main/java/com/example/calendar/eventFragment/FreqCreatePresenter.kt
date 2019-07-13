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

@InjectViewState
class FreqCreatePresenter(
    ruleString: String,
    start_: ZonedDateTime) :
    BaseMvpSubscribe<FreqCreateView>() {

    private val start = start_.withZoneSameInstant(ZoneId.systemDefault())

    var untilUTC = ZonedDateTime.now(ZoneId.systemDefault())
    val until : ZonedDateTime
        get() = untilUTC.withZoneSameInstant(ZoneId.systemDefault())

    var isNotRule : Boolean = ruleString.isEmpty()

    init {
        if (!isNotRule && RecurrenceRule(ruleString).until != null) {
            val rule = RecurrenceRule(ruleString)
            untilUTC = fromDateTimeUTC(rule.until)
        } else {
            untilUTC = start
                .plusDays(1)
                .truncatedTo(ChronoUnit.DAYS)
                .withZoneSameInstant(ZoneOffset.UTC)
        }
        viewState.setUntil(until)

        if (isNotRule) {
            viewState.setViewNotRule()
            viewState.setDayOfWeek(start.dayOfWeek)
        } else {
            val rule = RecurrenceRule(ruleString)
            viewState.setViewRule(rule)
        }
    }

    fun onUntilClick() {
        val l = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val newUntil = withYearMonthDay(until, year, monthOfYear, dayOfMonth)
                .truncatedTo(ChronoUnit.DAYS)
                .withZoneSameInstant(ZoneId.systemDefault())

            if (newUntil <= start) {
                viewState.showToast("Дата оконачания должна быть позже даты начала")
            } else {
                untilUTC = newUntil.withZoneSameInstant(ZoneOffset.UTC)
            }

            viewState.setUntil(until)
        }
        viewState.openCalendar(start, l)

    }

    fun onSelectItemFreq(pos: Int) {
        isNotRule = pos == eFreqView.NEVER.pos
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
