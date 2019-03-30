package com.example.calendar.dayCalendar

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.calendar.R
import kotlinx.android.synthetic.main.fragment_day_calendar.view.*
import java.text.SimpleDateFormat
import java.util.Calendar


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

        view.abfAddNote.setOnClickListener() { onClickAddNote() }
        return view
    }

    private fun onClickAddNote() {
//        val fragment = CreateEventFragment.newInstance()
//        activity?.supportFragmentManager
//            ?.beginTransaction()
//            ?.replace(
//                R.id.clMainContainer,
//                fragment
//            )
//            ?.addToBackStack(null)
//            ?.commit()
    }
}