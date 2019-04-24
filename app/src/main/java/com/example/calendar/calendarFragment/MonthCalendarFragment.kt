package com.example.calendar.calendarFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_month_calendar.view.*
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.example.calendar.remove.OpenView
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.customView.EventAdapter
import com.example.calendar.customView.MonthDotDecorator
import com.example.calendar.customView.TodayDecorator
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.data.EventTable
import java.text.SimpleDateFormat
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.calendar.remove.OpenCreateEventPresenter


class MonthCalendarFragment : MvpAppCompatFragment(),
    OpenView, CurrentDateView,
    ListEventView, MonthDotView,
    OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener {

    companion object {
        fun newInstance(): MonthCalendarFragment {
            return MonthCalendarFragment()
        }
    }

    @InjectPresenter
    lateinit var openCreateEventPresenter: OpenCreateEventPresenter

    @InjectPresenter
    lateinit var currentDatePresenter: CurrentDatePresenter

    @InjectPresenter
    lateinit var listEventPresenter: ListEventPresenter

    @ProvidePresenter
    fun provideListEventPresenter(): ListEventPresenter {
        return ListEventPresenter(
            // todo inject
            EventRoomDatabase.getInstance(context!!).eventDao()
        )
    }

    @InjectPresenter
    lateinit var monthDotPresenter: MonthDotPresenter

    @ProvidePresenter
    fun provideMonthEventPresenter(): MonthDotPresenter {
        return MonthDotPresenter(
            // todo inject
            EventRoomDatabase.getInstance(context!!).eventDao()
        )
    }

    private lateinit var v: View

    private val decorator = MonthDotDecorator()

    private val fmtCurDay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_month_calendar,
            container, false
        )

        v.cvMonthCalendar.setOnDateChangedListener(this)
        v.cvMonthCalendar.setOnDateLongClickListener(this);
        v.cvMonthCalendar.setOnMonthChangedListener(this);
        v.cvMonthCalendar.addDecorators(
            decorator,
            TodayDecorator(
                resources.getDrawable(
                    R.drawable.today_circle_background, null
                )
            )
        )

        v.abfAddNote.setOnClickListener { onClickAdfAddNote() }

        val linerLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            v.rvEventsMonthCalendar.context,
            linerLayoutManager.orientation
        )
        v.rvEventsMonthCalendar.run {
            this.adapter = EventAdapter { _, position ->
                onClickEvent(position)
            }
            this.layoutManager = linerLayoutManager
            this.addItemDecoration(dividerItemDecoration)
        }

        return v
    }

    private fun onClickAdfAddNote() {
        if (v.cvMonthCalendar.selectedDate == null) {
            openCreateEventPresenter.openOnTodayDay()
        } else {
            openCreateEventPresenter.openOnDay(
                v.cvMonthCalendar.selectedDate.calendar
            )
        }
    }

    private fun onClickEvent(pos: Int) {
        val id = listEventPresenter.getId(pos)
        // todo open
    }

    override fun onDateSelected(
        widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        currentDatePresenter.setCurrentDate(date.calendar)
    }

    override fun onDateLongClick(
        widget: MaterialCalendarView, date: CalendarDay) {
        openCreateEventPresenter.openOnDay(date.calendar)
    }

    override fun onMonthChanged(
        widget: MaterialCalendarView, date: CalendarDay) {
        monthDotPresenter.onMonthChange(date.calendar)
    }

    override fun openFragment(f: androidx.fragment.app.Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.clMainContainer, f)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun setMonthDots(it: HashSet<Calendar>) {
        decorator.setDates(it)
        v.cvMonthCalendar.invalidateDecorators();
    }

    override fun setEvents(it: List<EventTable>) {
        v.rvEventsMonthCalendar.adapter.run {
            (this as EventAdapter).setEvents(it)
        }
    }

    override fun setCurrentDate(date: Calendar) {
        v.cvMonthCalendar.selectedDate = CalendarDay.from(date)
        v.tvSelectDate.text = fmtCurDay.format(date.time)
        listEventPresenter.onDateSelected(date)
    }
}