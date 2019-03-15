package com.example.calendar.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.view.LayoutInflater


class CalendarDayView : FrameLayout {

    private var mDayHeight = 0

    private var mEventMarginLeft = 0

    private var mHourWidth = 120

    private var mTimeHeight = 120

    private var mSeparateHourHeight = 0

    private var mStartHour = 0

    private var mEndHour = 24

    private var mLayoutDayView: LinearLayout? = null


    constructor(context: Context) : super(context) {
//        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        init(attrs)
    }

//    private fun init(attrs: AttributeSet?) {
//        LayoutInflater.from(context).inflate(R.layout.view_day_calendar, this, true)
//
//        mLayoutDayView = findViewById(R.id.dayview_container) as LinearLayout
//        mDayHeight = resources.getDimensionPixelSize(R.dimen.dayHeight)
//
//        if (attrs != null) {
//            val a = context.obtainStyledAttributes(attrs, R.styleable.CalendarDayView)
//            try {
//                mEventMarginLeft = a.getDimensionPixelSize(
//                    R.styleable.CalendarDayView_eventMarginLeft,
//                    mEventMarginLeft
//                )
//                mDayHeight = a.getDimensionPixelSize(R.styleable.CalendarDayView_dayHeight, mDayHeight)
//                mStartHour = a.getInt(R.styleable.CalendarDayView_startHour, mStartHour)
//                mEndHour = a.getInt(R.styleable.CalendarDayView_endHour, mEndHour)
//            } finally {
//                a.recycle()
//            }
//        }
//        refresh()
//    }
//
//    fun refresh() {
//        drawDayViews()
//    }
//
//    private fun drawDayViews() {
//        mLayoutDayView!!.removeAllViews()
//        var dayView: DayView? = null
//        for (i in mStartHour..mEndHour) {
//            dayView = decoration!!.getDayView(i)
//            mLayoutDayView!!.addView(dayView)
//        }
//        mHourWidth = dayView!!.hourTextWidth.toInt()
//        mTimeHeight = dayView.hourTextHeight.toInt()
//        mSeparateHourHeight = dayView.separateHeight.toInt()
//    }
//
//    private fun getTimeBound(event: ITimeDuration): Rect {
//        val rect = Rect()
//        rect.top = getPositionOfTime(event.getStartTime()) + mTimeHeight / 2 + mSeparateHourHeight
//        rect.bottom = getPositionOfTime(event.getEndTime()) + mTimeHeight / 2 + mSeparateHourHeight
//        rect.left = mHourWidth + mEventMarginLeft
//        rect.right = width
//        return rect
//    }
//
//    private fun getPositionOfTime(calendar: Calendar): Int {
//        val hour = calendar.get(Calendar.HOUR_OF_DAY) - mStartHour
//        val minute = calendar.get(Calendar.MINUTE)
//        return hour * mDayHeight + minute * mDayHeight / 60
//    }
//
//
//    fun setLimitTime(startHour: Int, endHour: Int) {
//        if (startHour >= endHour) {
//            throw IllegalArgumentException("start hour must before end hour")
//        }
//        mStartHour = startHour
//        mEndHour = endHour
//        refresh()
//    }

}