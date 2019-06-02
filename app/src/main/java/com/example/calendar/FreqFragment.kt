package com.example.calendar

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.customView.MaterialDatePickerDialog
import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.withYearMonthDay
import kotlinx.android.synthetic.main.fragment_freq.view.*
import org.dmfs.rfc5545.recur.Freq
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

enum class FreqView(val pos: Int) {
    NEVER(0), DAILY(1), WEEKLY(2), MONTHLY(3), YEARLY(4);

    companion object {
        fun fromPos(pos: Int) : FreqView? {
            for (f in FreqView.values()) {
                if (f.pos == pos) {
                    return f
                }
            }
            return null
        }

        fun fromFreq(f: Freq) : FreqView? =
            when (f) {
                Freq.DAILY -> FreqView.DAILY
                Freq.WEEKLY -> FreqView.WEEKLY
                Freq.MONTHLY -> FreqView.MONTHLY
                Freq.YEARLY -> FreqView.YEARLY
                Freq.SECONDLY, Freq.MINUTELY, Freq.HOURLY -> null
            }


    }
}

enum class DurationView(val pos: Int) {
    INFINITELY(0), COUNT(1), UNTIL(2);

    companion object {
        fun fromPos(pos: Int) : DurationView? {
            for (f in DurationView.values()) {
                if (f.pos == pos) {
                    return f
                }
            }
            return null
        }

    }
}



class FreqFragment: MvpAppCompatFragment() {


    companion object {
        fun newInstance(freq: String = "") : FreqFragment {
            return FreqFragment()
        }
    }

    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_freq,
            container, false
        )
        setEnableGroup(true)

        v.spFreq.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long)
                    = selectedFreq(selectedItemPosition)

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

        v.spDuration.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long)
                    = selectedDuration(selectedItemPosition)

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

        v.tvDate.setOnClickListener { onDateClick() }

        // fromRule(RecurrenceRule("FREQ=DAILY;INTERVAL=2;COUNT=34"))

        return v
    }

    fun selectedFreq(selectedItemPosition: Int) {
        setEnableGroup(selectedItemPosition != FreqView.NEVER.pos)
    }

    fun selectedDuration(selectedItemPosition: Int) {
        v.etCount.visibility = getVisibility(selectedItemPosition == DurationView.COUNT.pos)

        v.tvDate.visibility = getVisibility(selectedItemPosition == DurationView.UNTIL.pos)
        v.tvDate.text = until.format(formatter)
    }

    fun fromRule(r: RecurrenceRule) {
        v.spFreq.setSelection(FreqView.fromFreq(r.freq)!!.pos)

        v.etEach.setText(r.interval.toString())

        if (r.count != null) {
            v.spDuration.setSelection(DurationView.COUNT.pos)
            v.etCount.setText(r.count.toString())
        } else if (r.until != null) {
            until = fromDateTimeUTC(r.until)
                .withZoneSameInstant(ZoneId.systemDefault())
            v.spDuration.setSelection(DurationView.UNTIL.pos)
        } else {
            v.spDuration.setSelection(DurationView.INFINITELY.pos)
        }
    }


    private fun setEnableGroup(enable: Boolean) {
        v.etEach.isEnabled = enable
        v.spDuration.isEnabled = enable
        v.tvDate.isEnabled = enable
        v.etCount.isEnabled = enable
    }

    private fun getVisibility(b: Boolean)
            =  if (b) View.VISIBLE else View.INVISIBLE

    private var until = ZonedDateTime.now(ZoneId.systemDefault())
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    private fun onDateClick() {
        val dpd = MaterialDatePickerDialog.newInstance(
            until,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                until = withYearMonthDay(until, year, monthOfYear, dayOfMonth)
                    .truncatedTo(ChronoUnit.DAYS)
                v.tvDate.text = until.format(formatter)
            })
        dpd.show(activity?.supportFragmentManager, "date-picker")
    }

}