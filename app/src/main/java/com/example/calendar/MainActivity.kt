package com.example.calendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.calendar.dayCalendar.DayCalendarFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.clMainContainer,
                MonthCalendarFragment.newInstance()
            )
            .addToBackStack(null)
            .commit()
    }
}