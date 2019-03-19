package com.example.calendar

import android.content.Context
import android.util.AttributeSet
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.view_day_calendar.view.*
import android.support.constraint.ConstraintSet
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class CalendarDayView
    @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var view: View = LayoutInflater.from(context).inflate(
            R.layout.view_day_calendar, this, true)

    val curDate = Calendar.getInstance();

//    private var mStartHour = 0
//    private val mDayHeight = 80
//    private val mHourWidth = 120
//    private val mSeparateHourHeight = 0
//    private val mTimeHeight = 120


    init {
        curDate.time = Date()
        updateText()
        updateHour()
        view.vPrev.setOnClickListener() { onSetPrevDate() }
        view.vNext.setOnClickListener() { onSetNextDate() }
    }

    private fun updateText() {
        val formatter = SimpleDateFormat("EE, dd MMM YYYY")
        view.tvDay.text = formatter.format(curDate.time)
    }

    private fun onSetPrevDate() {
        curDate.add(Calendar.DATE, -1)
        updateText()
    }

    private fun onSetNextDate() {
        curDate.add(Calendar.DATE, 1)
        updateText()
    }

    private fun onHourClick(hourBegin: Int, hourEnd: Int) {
        val text = "to ${hourBegin} from ${hourEnd}"
        Toast.makeText(context, text, Toast.LENGTH_SHORT)
    }

    private fun updateHour() {
//        view.llHourContainer.removeAllViews()
        var myHour = arrayOf("")
        for (i in 1..23) {
            myHour += i.toString()
        }
        var prevView : View = view.vDividerText
        for (i in 0..23) {
            val hour = HourItemView(context)
            hour.setHour(myHour[i])
//            hour.setOnClickListener() {
//                onHourClickbefore_breakfast_option(i, i+1)
//            }
//            view.llHourContainer.addView(hour)

            val cs = ConstraintSet()
            cs.clone(this)
            cs.connect(hour.id,
                ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT,0)
            cs.connect(hour.id,
                ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,0)

            cs.connect(hour.id, ConstraintSet.TOP, R.id.vDividerText, ConstraintSet.BOTTOM,0)
            cs.applyTo(this)

            prevView = hour
            this.addView(hour)
        }
    }

//    fun drawEvents() {
////        view.flEventContainer.removeAllViews()
//
////        for (event in mEvents) {
////            val begin = Calendar.getInstance()
////            begin.time = Date()
////            val end = begin
////            end.set(Calendar.HOUR, end.get(Calendar.HOUR) + 1)
////
////            val rect = getTimeBound(begin, end)
////
////            // add event view
////            val eventView = EventDayCalendarView(context)
////            eventView.setPosition(rect)
////            eventView.setEvent("TTT")
////            view.flEventContainer.addView(eventView) // eventView.layoutParan
////        }
//    }
//
//
//    private fun getTimeBound(start: Calendar, end: Calendar): Rect {
//        val rect = Rect()
//        rect.top = 10//getPositionOfTime(start) + mTimeHeight / 2 + mSeparateHourHeight
//        rect.bottom = 60//getPositionOfTime(end) + mTimeHeight / 2 + mSeparateHourHeight
//        rect.left = 0//mHourWidth // + mEventMarginLeft
//        rect.right = 500//width
//        return rect
//    }
//
//    private fun getPositionOfTime(calendar: Calendar): Int {
//        val hour = calendar.get(Calendar.HOUR_OF_DAY) - mStartHour
//        val minute = calendar.get(Calendar.MINUTE)
//        return hour * mDayHeight + minute * mDayHeight / 60
//    }

}