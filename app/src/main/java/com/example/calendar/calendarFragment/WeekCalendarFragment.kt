package com.example.calendar.calendarFragment

import android.widget.Toast
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.alamkanak.weekview.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.eventFragment.EditEventFragment
import com.example.calendar.R
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.helpers.TYPE_VIEW_KEY
import kotlinx.android.synthetic.main.fragment_week_calendar.view.*
import java.util.Calendar


import com.example.calendar.customView.EventWeekView
import com.example.calendar.remove.BackPressedPresenter
import com.example.calendar.remove.BackPressedView
import com.example.calendar.remove.OpenCreateEventPresenter
import com.example.calendar.remove.OpenView

class WeekCalendarFragment : MvpAppCompatFragment(),
    WeekEventView,
    OpenView, BackPressedView, WeekSaveStateView,
    EventClickListener<EventWeekView>, MonthChangeListener<EventWeekView>,
    EventLongPressListener<EventWeekView>, EmptyViewClickListener {

    enum class TypeView(val dayVisible: Int) {
        DAY(1),
        THREE_DAY(3),
        WEEK(7)
    }

    companion object {
        fun newInstance(type: TypeView): WeekCalendarFragment {
            val args = Bundle()
            args.run {
                this.putInt(TYPE_VIEW_KEY, type.dayVisible)
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
        val colorEvent = res.getColor(R.color.event_color_02)
        val colorFake = res.getColor(R.color.event_color_03)

        return WeekEventPresenter(
            // todo inject
            EventRoomDatabase.getInstance(context!!).eventDao(),
            colorEvent,
            colorFake
        )
    }

    @InjectPresenter
    lateinit var backPressedPresenter: BackPressedPresenter

    @InjectPresenter
    lateinit var addEventPresenter: OpenCreateEventPresenter

    @InjectPresenter
    lateinit var weekSaveStatePresenter: WeekSaveStatePresenter


    private lateinit var v: View
    private lateinit var wv: WeekView<EventWeekView>

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

        wv = v.findViewById(R.id.wvCalendar)
        wv.setOnEventClickListener(this)
        wv.setMonthChangeListener(this)
        wv.setEventLongPressListener(this)
        wv.emptyViewClickListener = this
        if (savedInstanceState == null) {
            wv.numberOfVisibleDays = arguments!!.getInt(TYPE_VIEW_KEY)
        }
        weekSaveStatePresenter.onCreateView()

        return v
    }

    override fun onStop() {
        weekSaveStatePresenter.onStop()
        super.onStop()
    }

    private fun initToolBar() {
        v.tbWeekCalendar.setNavigationOnClickListener() { backPressedPresenter.onBackPressed() }
        v.tbWeekCalendar.inflateMenu(R.menu.menu_week_calendar)
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
        }
    }

    override fun onEventClick(data: EventWeekView, eventRect: RectF) {
        // todo need presenter ??
        openFragment(EditEventFragment.newInstance(data.event.id))
    }

    // todo replace
    override fun onEventLongPress(data: EventWeekView, eventRect: RectF) {
        addEventPresenter.openFromTo(data.event.started_at, data.event.ended_at)
    }

    override fun onEmptyViewClicked(time: Calendar) {
        addEventPresenter.openOnTime(time)
    }

    override fun onMonthChange(startDate: Calendar, endDate: Calendar):
            List<WeekViewDisplayable<EventWeekView>> {
        // todo remove flickering events
        weekSaveStatePresenter.onMonthChange()
        return weekEventPresenter.onMonthChange(startDate)
    }

    override fun showLoading() {}

    override fun closeLoading() {}

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun notifySetChanged() {
        wv.notifyDataSetChanged()
    }

    override fun openFragment(f: androidx.fragment.app.Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.clMainContainer, f)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun updateState() {
        wv.hourHeight = weekSaveStatePresenter.hourHeight
        wv.goToHour(weekSaveStatePresenter.firstVisibleHour)
        wv.goToDate(weekSaveStatePresenter.firstVisibleDay)
    }

    override fun saveState() {
        weekSaveStatePresenter.firstVisibleHour = kotlin.math.floor(wv.firstVisibleHour).toInt()
        weekSaveStatePresenter.firstVisibleDay.timeInMillis = wv.firstVisibleDay.timeInMillis
        weekSaveStatePresenter.hourHeight = wv.hourHeight
    }

    override fun finishView() {
        activity!!.supportFragmentManager.popBackStack()
    }
}