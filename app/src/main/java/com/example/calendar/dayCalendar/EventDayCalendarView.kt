package com.example.calendar.dayCalendar

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.graphics.Rect
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.calendar.R
import kotlinx.android.synthetic.main.view_event_day_calendar.view.*


class EventDayCalendarView
    @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var view: View = LayoutInflater.from(context).inflate(
        R.layout.view_event_day_calendar, this, true)

    fun setEvent(title: String) {
        view.tvItemEventTitle.text = title
    }


    fun setPosition(rect: Rect, topMargin: Int=0, bottomMargin: Int=0) {
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.topMargin =
            rect.top + topMargin
        params.height = rect.height() + bottomMargin
        params.leftMargin = rect.left
        layoutParams = params
    }


}