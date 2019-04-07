package com.example.calendar

import android.widget.Toast
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alamkanak.weekview.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.helpers.TYPE_VIEW_KEY
import kotlinx.android.synthetic.main.fragment_week_calendar.view.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


import com.example.calendar.data.EventTable
import com.example.calendar.data.EventWeekCalendar


class WeekCalendarFragment : Fragment(),
    EventClickListener<EventWeekCalendar>, MonthChangeListener<EventWeekCalendar>,
    EventLongPressListener<EventWeekCalendar>, EmptyViewLongPressListener {

    enum class TypeView(val type: Int) {
        DAY(1),
        THREE_DAY(2),
        WEEK(3)
    }

    companion object {
        fun newInstance(type: TypeView): WeekCalendarFragment {
            val args = Bundle()
            args.run {
                this.putInt(TYPE_VIEW_KEY, type.type)
            }
            val f = WeekCalendarFragment()
            f.arguments = args
            return f
        }

    }

    private var calendarViewType = TypeView.THREE_DAY

    private lateinit var v: View

    private lateinit var wv: WeekView<EventWeekCalendar>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_week_calendar,
            container, false
        )
        initToolBar()
        // todo get type view

        wv = v.findViewById(R.id.wvCalendar)
        wv.setOnEventClickListener(this)
        wv.setMonthChangeListener(this)
        wv.setEventLongPressListener(this)
        wv.setEmptyViewLongPressListener(this)

        return v
    }

    private fun initToolBar() {
        v.tbWeekCalendar.setNavigationOnClickListener() { }
        v.tbWeekCalendar.inflateMenu(com.example.calendar.R.menu.menu_week_calendar)
        v.tbWeekCalendar.setOnMenuItemClickListener {
            onItemSelected(it);
            true
        }
    }

    private fun onItemSelected(it: MenuItem) {
        when (it.itemId) {
            R.id.action_today -> {
                wv.goToToday()
            }
            R.id.action_day_view -> {
                openDayView(it)
            }
            R.id.action_three_day_view -> {
                openThreeDayView(it)
            }
            R.id.action_week_view -> {
                openWeekView(it)
            }
        }
    }

    private fun openDayView(item: MenuItem) {
        if (calendarViewType === TypeView.DAY) {
            return
        }

        item.isChecked = !item.isChecked
        calendarViewType = TypeView.DAY
        wv.setNumberOfVisibleDays(1)
    }

    private fun openThreeDayView(item: MenuItem) {
        if (calendarViewType === TypeView.THREE_DAY) {
            return
        }

        item.isChecked = !item.isChecked
        calendarViewType = TypeView.THREE_DAY
        wv.setNumberOfVisibleDays(3)
    }

    private fun openWeekView(item: MenuItem) {
        if (calendarViewType === TypeView.WEEK) {
            return
        }

        item.isChecked = !item.isChecked
        calendarViewType = TypeView.WEEK
        wv.setNumberOfVisibleDays(7)
    }

    private fun getEventTitle(time: Calendar): String {
        return String.format(
            Locale.getDefault(),
            "Event of %02d:%02d %s/%d",
            time.get(Calendar.HOUR_OF_DAY),
            time.get(Calendar.MINUTE),
            time.get(Calendar.MONTH) + 1,
            time.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onEventClick(data: EventWeekCalendar, eventRect: RectF) {
        Toast.makeText(context, "Clicked " + data.event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEventLongPress(data: EventWeekCalendar, eventRect: RectF) {
        Toast.makeText(context, "Long pressed event: " + data.event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyViewLongPress(time: Calendar) {
        Toast.makeText(context, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show()
    }

    override fun onMonthChange(startDate: Calendar, endDate: Calendar): List<WeekViewDisplayable<EventWeekCalendar>> {
        val newYear = startDate.get(Calendar.YEAR)
        val newMonth = startDate.get(Calendar.MONTH)

        val color1 = context!!.getResources().getColor(R.color.event_color_01)
        val color2 = context!!.getResources().getColor(R.color.event_color_02)
        val color3 = context!!.getResources().getColor(R.color.event_color_03)
        val color4 = context!!.getResources().getColor(R.color.event_color_04)

        val events = ArrayList<EventWeekCalendar>()
        var event: EventWeekCalendar

        var startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 8)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        var endTime = startTime.clone() as Calendar
        endTime.add(Calendar.MINUTE, 30)
        endTime.set(Calendar.MONTH, newMonth)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color1
        )
        events.add(event)

        // Add multi-day event
        startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 3)
        startTime.set(Calendar.MINUTE, 30)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.add(Calendar.DAY_OF_MONTH, 1)
        endTime.set(Calendar.HOUR_OF_DAY, 4)
        endTime.set(Calendar.MINUTE, 30)
        endTime.set(Calendar.MONTH, newMonth)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color4
        )
        events.add(event)

        startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 3)
        startTime.set(Calendar.MINUTE, 30)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.set(Calendar.HOUR_OF_DAY, 4)
        endTime.set(Calendar.MINUTE, 30)
        endTime.set(Calendar.MONTH, newMonth)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color2
        )
        events.add(event)

        startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 4)
        startTime.set(Calendar.MINUTE, 30)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.set(Calendar.HOUR_OF_DAY, 5)
        endTime.set(Calendar.MINUTE, 0)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color3
        )
        events.add(event)

        startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 5)
        startTime.set(Calendar.MINUTE, 30)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR_OF_DAY, 2)
        endTime.set(Calendar.MONTH, newMonth)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color2
        )
        events.add(event)

        startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 5)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        startTime.add(Calendar.DATE, 1)
        endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR_OF_DAY, 3)
        endTime.set(Calendar.MONTH, newMonth)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color3
        )
        events.add(event)

        startTime = Calendar.getInstance()
        startTime.set(Calendar.DAY_OF_MONTH, 15)
        startTime.set(Calendar.HOUR_OF_DAY, 3)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR_OF_DAY, 3)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color4
        )
        events.add(event)

        startTime = Calendar.getInstance()
        startTime.set(Calendar.DAY_OF_MONTH, 1)
        startTime.set(Calendar.HOUR_OF_DAY, 3)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR_OF_DAY, 3)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color1
        )
        events.add(event)

        startTime = Calendar.getInstance()
        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH))
        startTime.set(Calendar.HOUR_OF_DAY, 15)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR_OF_DAY, 3)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color2
        )
        events.add(event)

        //AllDay event
        startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR_OF_DAY, 23)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color4
        )
        events.add(event)
        events.add(event)

        // All day event until 00:00 next day
        startTime = Calendar.getInstance()
        startTime.set(Calendar.DAY_OF_MONTH, 10)
        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.SECOND, 0)
        startTime.set(Calendar.MILLISECOND, 0)
        startTime.set(Calendar.MONTH, newMonth)
        startTime.set(Calendar.YEAR, newYear)
        endTime = startTime.clone() as Calendar
        endTime.set(Calendar.DAY_OF_MONTH, 11)

        event = EventWeekCalendar(
            EventTable(name = getEventTitle(startTime), started_at = startTime, ended_at = endTime),
            color1
        )
        events.add(event)

        return events
    }
}