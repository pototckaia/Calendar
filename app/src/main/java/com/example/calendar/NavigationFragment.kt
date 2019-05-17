package com.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.calendarFragment.MonthCalendarFragment
import com.example.calendar.calendarFragment.WeekCalendarFragment
import com.example.calendar.navigation.CiceroneApplication
import com.example.calendar.navigation.Screens
import kotlinx.android.synthetic.main.fragment_navigation.view.*
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

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
            // todo inject
            CiceroneApplication.instance.router.navigateTo(Screens.MonthCalendarScreen())
        }

        v.bWeek.setOnClickListener {
            // todo inject
            CiceroneApplication.instance.router.navigateTo(
                Screens.WeekCalendarScreen(WeekCalendarFragment.TypeView.WEEK))
        }

        v.bDay.setOnClickListener {
            // todo inject
            CiceroneApplication.instance.router.navigateTo(
                Screens.WeekCalendarScreen(WeekCalendarFragment.TypeView.DAY))
        }

        v.bTest.setOnClickListener {
            CiceroneApplication.instance.router.navigateTo(
                Screens.TestDelete()
            )
        }

        return v
    }
}