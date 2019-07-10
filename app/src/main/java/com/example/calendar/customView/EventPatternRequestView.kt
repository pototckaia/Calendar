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
import android.text.InputType
import android.widget.Toast
import com.example.calendar.R
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneOffset
import java.util.*

class EventPatternRequestView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    constructor(
        start: ZonedDateTime, end: ZonedDateTime,
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : this(context, attrs, defStyleAttr) {
        setDate(start, end)
    }

    private var v: View = LayoutInflater.from(context).inflate(
        R.layout.view_event_pattern_request, this, true
    )

    var start: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
    var end: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EventPatternRequestView)
        attr.recycle()

        setDate(start, end)
        v.etTimezone.setText(ZoneId.systemDefault().toString())
        v.vBegin.onDayClickListener = OnClickListener { onClickBeginDay() }
        v.vBegin.onHourClickListener = OnClickListener { onClickBeginHour() }
        v.vEnd.onDayClickListener = OnClickListener { onClickEndDay() }
        v.vEnd.onHourClickListener = OnClickListener { onClickEndHour() }
    }

    fun getPattern() = PatternRequest(
        started_at = start.withZoneSameInstant(ZoneOffset.UTC),
        ended_at = end.withZoneSameInstant(ZoneOffset.UTC),
        rrule = v.etRecurrenceRule.text.toString(),
        timezone = ZoneId.of(v.etTimezone.text.toString()),
        exrules = emptyList()
    )

    fun setDate(s: ZonedDateTime, e: ZonedDateTime) {
        start = ZonedDateTime.from(s)
        end = ZonedDateTime.from(e)
        updateDateInfo(s, e)
    }

    fun setPattern(e: PatternRequest) {
        setDate(e.started_at, e.started_at.plus(e.duration))
        v.etRecurrenceRule.setText(e.rrule)
        v.etTimezone.setText(e.timezone.toString())
    }

    fun setRecurrence(r: String) {
        v.etRecurrenceRule.setText(r)
    }

    private fun updateDateInfo(startLocal: ZonedDateTime, endLocal: ZonedDateTime) {
        v.vBegin.setDate(startLocal)
        v.vEnd.setDate(endLocal)
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
                updateDateInfo(start, end)
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
                updateDateInfo(start, end)
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
                updateDateInfo(start, end)
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
                updateDateInfo(start, end)
            })
    }
}