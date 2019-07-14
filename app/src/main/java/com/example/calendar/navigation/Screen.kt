package com.example.calendar.navigation

import android.content.Context
import ru.terrakok.cicerone.android.support.SupportAppScreen
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.calendar.eventFragment.FreqCreateFragment
import com.example.calendar.MainActivity
import com.example.calendar.NavigationFragment
import com.example.calendar.calendarFragment.MonthCalendarFragment
import com.example.calendar.calendarFragment.WeekCalendarFragment
import com.example.calendar.eventFragment.CreateEventFragment
import com.example.calendar.eventFragment.EditEventInstanceFragment
import com.example.calendar.FirebaseUIActivity
import com.example.calendar.eventFragment.TimeZoneSelectFragment
import com.example.calendar.repository.server.model.EventInstance
import org.threeten.bp.ZonedDateTime

class Screens {

    class EventScreen(private val event: EventInstance) : SupportAppScreen() {
        init {
            screenKey = "${javaClass.simpleName}_${event.entity.id}"
        }

        override fun getFragment(): Fragment =
            EditEventInstanceFragment.newInstance(event)
    }

    class NewEventScreen(
        private val s: ZonedDateTime,
        private val e: ZonedDateTime
    ) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            CreateEventFragment.newInstance(s, e)
    }

    class MonthCalendarScreen : SupportAppScreen() {
        override fun getFragment(): Fragment =
            MonthCalendarFragment.newInstance()
    }

    class WeekCalendarScreen(private val type: WeekCalendarFragment.TypeView) : SupportAppScreen() {
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
        override fun getFragment(): Fragment =
            NavigationFragment.newInstance()
    }

    class FreqScreen(
        private val start: ZonedDateTime,
        private val rule: String = ""
    ) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            FreqCreateFragment.newInstance(start, rule)
    }

    class AuthScreen : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent {
            return Intent(context, FirebaseUIActivity::class.java)
        }
    }

    class TimeZoneSelectScreen : SupportAppScreen() {
        override fun getFragment(): Fragment =
                TimeZoneSelectFragment.newInstance()
    }
}