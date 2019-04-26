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
import com.example.calendar.customView.ListEventDialog
import com.example.calendar.remove.BackPressedPresenter
import com.example.calendar.remove.BackPressedView
import com.example.calendar.remove.OpenCreateEventPresenter
import com.example.calendar.remove.OpenView

class WeekCalendarFragment : MvpAppCompatFragment(),
    WeekEventView,
    OpenView, BackPressedView, WeekSaveStateView,
    EventClickListener<EventWeekView>, MonthChangeListener<EventWeekView>,
    EmptyViewLongPressListener, EmptyViewClickListener {

    enum class TypeView(val dayVisible: Int, val maxIntersection: Int) {
        DAY(1, 4),
        THREE_DAY(3, 3),
        WEEK(7, 2)
    }

    companion object {
        fun newInstance(type: TypeView): WeekCalendarFragment {
            val args = Bundle()
            args.run {
                this.putString(TYPE_VIEW_KEY, type.toString())
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
        val resources = context!!.resources
        val typeView = TypeView.valueOf(arguments!!.getString(TYPE_VIEW_KEY)!!)
        return WeekEventPresenter(
            // todo inject
            EventRoomDatabase.getInstance(context!!).eventDao(),
            typeView.maxIntersection,
            resources.getColor(R.color.event),
            resources.getColor(R.color.intersection_event),
            resources.getColor(R.color.fake_event),
            resources.getString(R.string.title_fake_event)
        )
    }

    @InjectPresenter
    lateinit var backPressedPresenter: BackPressedPresenter

    @InjectPresenter
    lateinit var openCreateEventPresenter: OpenCreateEventPresenter

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

        // todo make global
        val typeView = TypeView.valueOf(arguments!!.getString(TYPE_VIEW_KEY)!!)

        wv = v.findViewById(R.id.wvCalendar)
        wv.setOnEventClickListener(this)
        wv.setMonthChangeListener(this)
        wv.emptyViewLongPressListener = this
        wv.emptyViewClickListener = this
        if (savedInstanceState == null) {
            wv.numberOfVisibleDays = typeView.dayVisible
        }
        weekSaveStatePresenter.onCreateView()

        return v
    }

    override fun onStop() {
        weekSaveStatePresenter.onStop()
        super.onStop()
    }

    private fun initToolBar() {
        v.tbWeekCalendar.setNavigationOnClickListener { backPressedPresenter.onBackPressed() }
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
        if (data.isFake) {
            val d = ListEventDialog.newInstance(data.event.started_at, data.event.ended_at)
            d.show(activity?.supportFragmentManager, "list-dialog")
        } else {
            openFragment(EditEventFragment.newInstance(data.event.id))
        }
    }

    override fun onEmptyViewLongPress(time: Calendar) {
        openCreateEventPresenter.openOnTime(time)
    }

    override fun onEmptyViewClicked(time: Calendar) {
        openCreateEventPresenter.openOnTime(time)
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