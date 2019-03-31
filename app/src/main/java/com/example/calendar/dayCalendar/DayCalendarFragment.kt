package com.example.calendar.dayCalendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.calendar.R
import com.example.calendar.helpers.getCalendarWithUTF
import kotlinx.android.synthetic.main.fragment_day_calendar.view.*
import java.text.SimpleDateFormat
import java.util.Calendar


class DayCalendarFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance(): DayCalendarFragment {
            return DayCalendarFragment()
        }
    }

    private val calendar = getCalendarWithUTF()
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