package com.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.calendarFragment.MonthCalendarFragment
import com.example.calendar.calendarFragment.WeekCalendarFragment
import kotlinx.android.synthetic.main.fragment_navigation.view.*

class NavigationFragment : MvpAppCompatFragment() {

    companion object {
        fun newInstance() : NavigationFragment {
            return NavigationFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(
            R.layout.fragment_navigation,
            container, false
        )

        v.bMonth.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(
                    R.id.clMainContainer,
                    MonthCalendarFragment.newInstance())
                ?.addToBackStack(null)
                ?.commit()
        }

        v.bWeek.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(
                    R.id.clMainContainer,
                    WeekCalendarFragment.newInstance(WeekCalendarFragment.TypeView.WEEK)
                )
                ?.addToBackStack(null)
                ?.commit()
        }

        v.bDay.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(
                    R.id.clMainContainer,
                    WeekCalendarFragment.newInstance(WeekCalendarFragment.TypeView.DAY)
                )
                ?.addToBackStack(null)
                ?.commit()
        }

        return v
    }
}