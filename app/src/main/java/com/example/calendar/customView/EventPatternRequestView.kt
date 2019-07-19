package com.example.calendar.customView

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import com.example.calendar.helpers.*
import com.example.calendar.repository.server.model.PatternRequest
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.calendar.R
import com.example.calendar.helpers.convert.fromDateTimeUTC
import com.example.calendar.inject.InjectApplication
import com.example.calendar.helpers.getRecurrenceName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.view_event_pattern_request.view.*
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

@Parcelize
data class EventPatternViewState(
    val state: PatternRequest?
): MvvmViewState

class EventPatternViewModel: MvvmViewModel<EventPatternViewState> {
    val liveData = MutableLiveData<PatternRequest?>()

    override var state: EventPatternViewState? = null
        get() = EventPatternViewState(liveData.value)
        set(value) {
            field = value
            restore(value)
        }

    private fun restore(state: EventPatternViewState?) {
        liveData.value = state?.state
    }

    fun setRecurrence(r: String?) {
        liveData.value?.setRecurrence(r)
        liveData.value = liveData.value
    }

    fun setTimeZone(z: ZoneId) {
        liveData.value?.timezone = z
        liveData.value = liveData.value
    }

    fun setDateView(start: ZonedDateTime, end: ZonedDateTime) {
        liveData.value?.setStartedAt(start)
        liveData.value?.set_duration(end)
        liveData.value = liveData.value
    }
}


class EventPatternRequestView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MvvmConstraintLayout<EventPatternViewState, EventPatternViewModel>(context, attrs, defStyleAttr) {


    override val viewModel = EventPatternViewModel()

    private val eventPattern: PatternRequest
        get() = viewModel.liveData.value!!

    init {
        View.inflate(context, R.layout.view_event_pattern_request, this)
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EventPatternRequestView)
        attr.recycle()

        etTimezone.inputType = InputType.TYPE_NULL

        vBegin.onDayClickListener = OnClickListener { onClickBeginDay() }
        vBegin.onHourClickListener = OnClickListener { onClickBeginHour() }
        vEnd.onDayClickListener = OnClickListener { onClickEndDay() }
        vEnd.onHourClickListener = OnClickListener { onClickEndHour() }
    }

    fun setTrashInvisible() {
        ivDelete.visibility = View.GONE
    }

    fun setRecurrenceOnClick(s : (p: PatternRequest) -> Unit) {
        tvRecurrenceRule.setOnClickListener { s(eventPattern) }
    }

    fun setTimeZoneOnClick(s : () -> Unit) {
        etTimezone.setOnClickListener { s() }
        etTimezone.setOnFocusChangeListener { _, b -> if (b) s() }
    }

    override fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner) {
        observeLiveData(lifecycleOwner)
    }

    private fun observeLiveData(lifecycleOwner: LifecycleOwner) {
        viewModel.liveData.observe(lifecycleOwner, Observer {
            updateView(it!!)
        })
    }

    private fun updateView(p: PatternRequest) {
        vBegin.setDate(p.startedAtTimezone)
        vEnd.setDate(p.endedAtAtTimezone)
        setDefaultDateView(p.timezone)
        tvRecurrenceRule.setText(getRecurrenceName(p.rrule))
        etTimezone.setText(getTimeZoneName(p.timezone))
        setDefaultDateView(p.timezone)
    }

    private fun setDefaultDateView(timezone: ZoneId) {
        val zoneDefault = ZoneId.systemDefault()
        if (timezone == zoneDefault) {
            tvDefaultDate.setText("")
            return
        }

        val nameDefault = getTimeZoneName(zoneDefault)
        val startDefault = eventPattern.startedAtTimezone.withZoneSameInstant(zoneDefault)
        val endDefault = eventPattern.endedAtAtTimezone.withZoneSameInstant(zoneDefault)
        val diffDay = getStringDayDiff(startDefault, endDefault)
        val diffHour = getStringDiff(startDefault, endDefault, "HH:mm")
        tvDefaultDate.setText("$nameDefault : \n $diffDay $diffHour")
    }

    private fun showDatePickerDialog(local: ZonedDateTime, l: DatePickerDialog.OnDateSetListener) {
        val dpd = MaterialDatePickerDialog(local, l, context,
            DialogInterface.OnCancelListener { _: DialogInterface -> })
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
        val rule = eventPattern.rrule
        if (rule != null && RecurrenceRule(rule).until != null) {
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
            eventPattern.startedAtTimezone,
            // month start from 1
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newStart = withYearMonthDay(eventPattern.startedAtTimezone, year, monthOfYear, dayOfMonth)
                if (!validateStart(newStart)) {
                    return@OnDateSetListener
                }

                var newEnd = ZonedDateTime.from(eventPattern.endedAtAtTimezone)
                if (newStart > newEnd) {
                    Toast.makeText(context, "Начало не может быть позже конца", Toast.LENGTH_LONG).show()
                    newEnd = withYearMonthDay(newEnd, year, monthOfYear, dayOfMonth)
                }
                viewModel.setDateView(newStart, newEnd)
            })
    }

    private fun onClickBeginHour() {
        showTimePickerDialog(
            eventPattern.startedAtTimezone,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val newStart = withHourMinuteTruncate(eventPattern.startedAtTimezone, hourOfDay, minute)
                if (!validateStart(newStart)) {
                    return@OnTimeSetListener
                }

                var newEnd = ZonedDateTime.from(eventPattern.endedAtAtTimezone)
                if (newStart >= newEnd) {
                    Toast.makeText(context, "Начало не может быть позже конца", Toast.LENGTH_LONG).show()
                    newEnd = newStart.plusHours(1)
                }
                viewModel.setDateView(newStart, newEnd)
            })
    }

    private fun onClickEndDay() {
        showDatePickerDialog(
            eventPattern.endedAtAtTimezone,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newEnd = withYearMonthDay(eventPattern.endedAtAtTimezone, year, monthOfYear, dayOfMonth)
                if (!validateEnd(newEnd)) {
                    return@OnDateSetListener
                }

                var newStart = ZonedDateTime.from(eventPattern.startedAtTimezone)
                if (newEnd < newStart) {
                    Toast.makeText(context, "Конец не может быть раньше начала", Toast.LENGTH_LONG).show()
                    newStart = withYearMonthDay(newStart, year, monthOfYear, dayOfMonth)
                }
                viewModel.setDateView(newStart, newEnd)
            })
    }

    private fun onClickEndHour() {
        showTimePickerDialog(
            eventPattern.endedAtAtTimezone,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val newEnd = withHourMinuteTruncate(eventPattern.endedAtAtTimezone, hourOfDay, minute)
                if (!validateEnd(newEnd)) {
                    return@OnTimeSetListener
                }

                var newStart = ZonedDateTime.from(eventPattern.startedAtTimezone)
                if (newEnd < newStart) {
                    Toast.makeText(context, "Конец не может быть раньше начала", Toast.LENGTH_LONG).show()
                    newStart = newEnd.minusHours(1)
                }
                viewModel.setDateView(newStart, newEnd)
            })
    }
}