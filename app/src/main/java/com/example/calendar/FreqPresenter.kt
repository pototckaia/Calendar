package com.example.calendar

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
    private val ruleString: String,
    start_: ZonedDateTime,
    private val router: Router) :
    BaseMvpSubscribe<RecurrenceRuleView >() {

    private val start = start_.withZoneSameInstant(ZoneId.systemDefault())

    var untilUTC = ZonedDateTime.now(ZoneId.systemDefault())
    val until : ZonedDateTime
        get() = untilUTC.withZoneSameInstant(ZoneId.systemDefault())

    var isNotRule : Boolean = ruleString.isEmpty()

    init {
        if (isNotRule) {
            viewState.setViewNotRule()
        } else {
            val rule = RecurrenceRule(ruleString)
            if (rule.until != null) {
                untilUTC = fromDateTimeUTC(rule.until)
            }
            viewState.setViewRule(rule)
        }
    }

    fun onUntilClick() {
        val l = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            untilUTC = withYearMonthDay(until, year, monthOfYear, dayOfMonth)
                .truncatedTo(ChronoUnit.DAYS)
                .withZoneSameInstant(ZoneId.systemDefault())
            if (until < start) {
                untilUTC = start.withZoneSameInstant(ZoneOffset.UTC)
                // todo hard
                viewState.showToast("Дата оконачания должна быть позже даты начала")
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

        } else {
            val r = RecurrenceRule("FREQ=DAILY;")
            viewState.onSave(r)
            viewState.showToast(r.toString())
        }
    }
}
