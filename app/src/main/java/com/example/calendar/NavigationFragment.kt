package com.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.calendarFragment.WeekCalendarFragment.TypeView
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import com.firebase.ui.auth.AuthUI
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
            InjectApplication.inject.router.navigateTo(Screens.MonthCalendarScreen())
        }

        v.bWeek.setOnClickListener {
            InjectApplication.inject.router.navigateTo(
                Screens.WeekCalendarScreen(TypeView.WEEK))
        }

        v.bDay.setOnClickListener {
            InjectApplication.inject.router.navigateTo(
                Screens.WeekCalendarScreen(TypeView.DAY))
        }

        v.bSignOut.setOnClickListener {
            AuthUI.getInstance()
                .signOut(context!!)
                .addOnCompleteListener {
                    InjectApplication.inject.router.navigateTo(Screens.AuthScreen())
                }

        }

        return v
    }
}