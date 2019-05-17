package com.example.calendar.navigation

import android.content.Context
import ru.terrakok.cicerone.android.support.SupportAppScreen
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.calendar.MainActivity
import com.example.calendar.NavigationFragment
import com.example.calendar.RecurrenceFragmentTest
import com.example.calendar.calendarFragment.MonthCalendarFragment
import com.example.calendar.calendarFragment.WeekCalendarFragment
import com.example.calendar.eventFragment.CreateEventFragment
import com.example.calendar.eventFragment.EditEventFragment
import java.util.Calendar

class Screens {

    class EventScreen(private val id: String) : SupportAppScreen() {
        init {
            screenKey = "${javaClass.simpleName}_$id"
        }

        override fun getFragment(): Fragment =
            EditEventFragment.newInstance(id)
    }

    class NewEventScreen(private val s: Calendar,
                         private val e: Calendar) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            CreateEventFragment.newInstance(s, e)
    }

    class MonthCalendarScreen : SupportAppScreen() {
        override fun getFragment() : Fragment =
                MonthCalendarFragment.newInstance()
    }

    class WeekCalendarScreen(private val type: WeekCalendarFragment.TypeView)
        : SupportAppScreen() {
        init {
            screenKey = "${javaClass.simpleName}_$type"
        }

        override fun getFragment(): Fragment =
                WeekCalendarFragment.newInstance(type)
    }

    class MainScreen : SupportAppScreen() {
        override fun getActivityIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
    }

    class NavigationScreen : SupportAppScreen() {
        override fun getFragment() : Fragment =
            NavigationFragment.newInstance()
    }

    class TestDelete : SupportAppScreen() {
        override fun getFragment() : Fragment =
            RecurrenceFragmentTest()
    }

//    class GithubScreen : SupportAppScreen() {
//        fun getActivityIntent(context: Context): Intent {
//            return Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/terrakok/Cicerone"))
//        }
//    }
}