package com.example.calendar

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.calendar.MyAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_day_calendar.view.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class DayCalendarFragment : Fragment() {

    companion object {
        fun newInstance(): DayCalendarFragment {
            return DayCalendarFragment()
        }
    }

    private val calendar = Calendar.getInstance()
    private val dayOfWeekFormatter = SimpleDateFormat("EEE")
    private val dayFormatter = SimpleDateFormat("MMMM dd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(
            R.layout.fragment_day_calendar,
            container, false
        )

        val arguments = arguments
        if (arguments != null && arguments.containsKey(DATE_DAY_CALENDAR)) {
            calendar.timeInMillis = arguments.getLong(DATE_DAY_CALENDAR)
        } else {
            calendar.timeInMillis = Date().time
        }

        // TODO: error on change orientation
        var myDataset = arrayOf("")
        for (i in 1..23) {
            myDataset += i.toString()
        }
        view.tvDayOfWeek.text = dayOfWeekFormatter.format(calendar.time)
        view.tvDay.text = dayFormatter.format(calendar.time)

        view.rvDayCalendar.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MyAdapter(myDataset)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.clearFindViewByIdCache()
    }

    fun onClickAddNote(v: View?) {  }
}