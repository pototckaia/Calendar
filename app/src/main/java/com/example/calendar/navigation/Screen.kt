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
import com.example.calendar.eventFragment.EventFragment
import com.example.calendar.auth.FirebaseSignInActivity
import com.example.calendar.eventFragment.TimeZoneSelectFragment
import com.example.calendar.permission.CreateEventPermissionFragment
import com.example.calendar.permission.PermissionListFragment
import com.example.calendar.repository.server.model.EventInstance
import org.threeten.bp.ZonedDateTime

class Screens {

    class EventScreen(private val event: EventInstance) : SupportAppScreen() {
        init {
            screenKey = "${javaClass.simpleName}_${event.entity_id}"
        }

        override fun getFragment(): Fragment =
            EventFragment.newInstance(event)
    }

    class NewEventScreen(
        private val s: ZonedDateTime,
        private val e: ZonedDateTime
    ) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            EventFragment.newInstance(s, e)
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
        private val rule: String? = null
    ) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            FreqCreateFragment.newInstance(start, rule)
    }

    class AuthScreen : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent {
            return Intent(context, FirebaseSignInActivity::class.java)
        }
    }

    class TimeZoneSelectScreen : SupportAppScreen() {
        override fun getFragment(): Fragment =
                TimeZoneSelectFragment.newInstance()
    }

    class PermissionListScreen : SupportAppScreen() {
        override fun getFragment(): Fragment =
                PermissionListFragment.newInstance()
    }

    class CreateEventPermissionScreen(
        val entity_id: Long,
        val pattern_ids: List<Long>
    ): SupportAppScreen() {

        override fun getFragment(): Fragment =
                CreateEventPermissionFragment.newInstance(entity_id, pattern_ids)

    }
}