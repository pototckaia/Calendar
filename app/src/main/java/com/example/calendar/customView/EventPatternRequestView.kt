package com.example.calendar.customView

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.calendar.helpers.*
import com.example.calendar.repository.server.model.PatternRequest
import kotlinx.android.synthetic.main.view_event_pattern_request.view.*
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import android.os.Parcelable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.calendar.R
import com.example.calendar.inject.InjectApplication
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneOffset

private fun getTimeZoneName(z: ZoneId) : String {
    val id = z.toString()
    val timezoneName = InjectApplication.inject.timezoneName
    if (timezoneName.containsKey(id)) {
        return timezoneName[id]!!
    } else {
        return id
    }
}

// todo make model view
class EventPatternRequestView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var v: View = LayoutInflater.from(context).inflate(
        R.layout.view_event_pattern_request, this, true
    )

    // in timezone
    var start = ZonedDateTime.now()
        private set
    var end = ZonedDateTime.now()
        private set
    var recurrence = ""
        private set
    var timezone = ZoneId.systemDefault()
        private set

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EventPatternRequestView)
        attr.recycle()

        updateView()

        v.vBegin.onDayClickListener = OnClickListener { onClickBeginDay() }
        v.vBegin.onHourClickListener = OnClickListener { onClickBeginHour() }
        v.vEnd.onDayClickListener = OnClickListener { onClickEndDay() }
        v.vEnd.onHourClickListener = OnClickListener { onClickEndHour() }
    }

    fun getPattern() = PatternRequest(
        started_at = start.withZoneSameInstant(ZoneOffset.UTC),
        ended_at = end.withZoneSameInstant(ZoneOffset.UTC),
        rrule = recurrence,
        timezone = timezone,
        exrules = emptyList()
    )

    fun setPattern(e: PatternRequest) {
        start = e.startedAtTimezone
        end = e.endedAtAtTimezone
        timezone = e.timezone
        recurrence = e.rrule

        updateView()
    }

    private fun updateView() {
        setDateView(start, end)
        v.etRecurrenceRule.setText(recurrence)
        v.etTimezone.setText(getTimeZoneName(timezone))
        setDefaultDateView(timezone)
    }

    private fun setDateView(startLocal: ZonedDateTime, endLocal: ZonedDateTime) {
        v.vBegin.setDate(startLocal)
        v.vEnd.setDate(endLocal)
        setDefaultDateView(timezone)
    }

    private fun setDefaultDateView(timezone: ZoneId) {
        val zoneDefault = ZoneId.systemDefault()
        if (timezone == zoneDefault) {
            v.tvDefaultDate.setText("")
            return
        }

        val nameDefault = getTimeZoneName(zoneDefault)
        val startDefault = start.withZoneSameInstant(zoneDefault)
        val endDefault = end.withZoneSameInstant(zoneDefault)
        val diffDay = getStringDayDiff(startDefault, endDefault)
        val diffHour = getStringDiff(startDefault, endDefault, "HH:mm")
        v.tvDefaultDate.setText("$nameDefault : \n $diffDay $diffHour")
    }

    private fun showDatePickerDialog(local: ZonedDateTime, l: DatePickerDialog.OnDateSetListener) {
        val dpd = MaterialDatePickerDialog(local, l, context,
            DialogInterface.OnCancelListener { var1: DialogInterface -> })
        dpd.show()
    }

    private fun showTimePickerDialog(local: ZonedDateTime, l: TimePickerDialog.OnTimeSetListener) {
        val tpd = TimePickerDialog(
            context, l,
            local.hour,
            local.minute,
            true
        )
        tpd.show()
    }

    private fun validateStart(start: ZonedDateTime): Boolean {
        val rule = v.etRecurrenceRule.text.toString()
        if (rule.isNotEmpty() && RecurrenceRule(rule).until != null) {
            val until = fromDateTimeUTC(RecurrenceRule(rule).until)
            val startUTC = start.withZoneSameInstant(ZoneOffset.UTC)
            if (startUTC >= until) {
                Toast
                    .makeText(
                        context,
                        "Дата начала события не может быть позже даты ДО в правиле переодичности",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
            return startUTC < until
        }
        return true
    }

    private fun validateEnd(end: ZonedDateTime): Boolean {
        return true
    }

    private fun onClickBeginDay() {
        showDatePickerDialog(
            start,
            // month start from 1
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // TODO check it work
                val newStart = withYearMonthDay(start, year, monthOfYear, dayOfMonth)
                if (!validateStart(newStart)) {
                    return@OnDateSetListener
                }

                start = newStart
                if (start > end) {
                    end = withYearMonthDay(end, year, monthOfYear, dayOfMonth)
                }
                setDateView(start, end)
            })
    }

    private fun onClickBeginHour() {
        showTimePickerDialog(
            start,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val newStart = withHourMinuteTruncate(start, hourOfDay, minute)
                if (!validateStart(newStart)) {
                    return@OnTimeSetListener
                }

                start = newStart
                if (start >= end) {
                    end = start.plusHours(1)
                }
                setDateView(start, end)
            })
    }

    private fun onClickEndDay() {
        showDatePickerDialog(
            end,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newEnd = withYearMonthDay(end, year, monthOfYear, dayOfMonth)
                if (!validateEnd(newEnd)) {
                    return@OnDateSetListener
                }

                end = newEnd
                if (end < start) {
                    start = withYearMonthDay(start, year, monthOfYear, dayOfMonth)
                }
                setDateView(start, end)
            })
    }

    private fun onClickEndHour() {
        showTimePickerDialog(
            end,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val newEnd = withHourMinuteTruncate(end, hourOfDay, minute)
                if (!validateEnd(newEnd)) {
                    return@OnTimeSetListener
                }

                end = newEnd
                if (end <= start) {
                    start = end.minusHours(1)
                }
                setDateView(start, end)
            })
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable("superState", superState)
        bundle.putParcelable("pattern", getPattern())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var superState = state
        if (state is Bundle) {
            setPattern(state.getParcelable("pattern") as PatternRequest)
            superState = state.getParcelable("superState")
        }
        super.onRestoreInstanceState(superState)
    }
}