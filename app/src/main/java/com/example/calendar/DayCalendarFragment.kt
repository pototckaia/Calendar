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


class DayCalendarFragment : Fragment() {

    companion object {
        fun newInstance(): DayCalendarFragment {
            return DayCalendarFragment()
        }
    }

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
//        calendarView.setOnDateChangeListener(this)
//        abfAddNote = view.findViewById(R.id.abfAddNote)
//        abfAddNote.setOnClickListener()

        var myDataset = arrayOf("")
        for (i in 1..23) {
            myDataset += i.toString()
        }
        view.tvDayOfWeek.text = "CБ"
        view.tvDay.text = "11"

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

    fun onClickAddNote(v: View?) {

    }
}