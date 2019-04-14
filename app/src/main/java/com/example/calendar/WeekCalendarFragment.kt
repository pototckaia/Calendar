package com.example.calendar

import android.widget.Toast
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.alamkanak.weekview.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.helpers.TYPE_VIEW_KEY
import kotlinx.android.synthetic.main.fragment_week_calendar.view.*
import java.util.Calendar
import java.util.Locale


import com.example.calendar.data.EventWeekCalendar
import com.example.calendar.presenter.WeekEventPresenter
import com.example.calendar.view.WeekEventView


class WeekCalendarFragment : MvpAppCompatFragment(),
    WeekEventView,
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

    @InjectPresenter
    lateinit var weekEventPresenter: WeekEventPresenter

    @ProvidePresenter
    fun providerWeekEventPresenter(): WeekEventPresenter {
        val res = context!!.resources
        // todo replace
        val color = listOf(
            res.getColor(R.color.event_color_02),
            res.getColor(R.color.event_color_03),
            res.getColor(R.color.event_color_01)
        )
        return WeekEventPresenter(
            EventRoomDatabase.getInstance(context!!).eventDao(),
            color
        )
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

    // todo remove
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

    // todo remove
    private fun openDayView(item: MenuItem) {
        if (calendarViewType === TypeView.DAY) {
            return
        }

        item.isChecked = !item.isChecked
        calendarViewType = TypeView.DAY
        wv.setNumberOfVisibleDays(1)
    }

    // todo remove
    private fun openThreeDayView(item: MenuItem) {
        if (calendarViewType === TypeView.THREE_DAY) {
            return
        }

        item.isChecked = !item.isChecked
        calendarViewType = TypeView.THREE_DAY
        wv.setNumberOfVisibleDays(3)
    }

    // todo remove
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
        // todo add
        Toast.makeText(context, "Clicked " + data.event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEventLongPress(data: EventWeekCalendar, eventRect: RectF) {
        // todo add
        Toast.makeText(context, "Long pressed event: " + data.event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyViewLongPress(time: Calendar) {
        // todo add
        Toast.makeText(context, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show()
    }

    override fun onMonthChange(startDate: Calendar, endDate: Calendar):
            List<WeekViewDisplayable<EventWeekCalendar>> {
        return weekEventPresenter.onMonthChange(startDate)
    }

    override fun showLoadingEvents() {}

    override fun closeLoadingEvents() {}

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun notifyEventSetChanged() {
        wv.notifyDataSetChanged()
    }
}