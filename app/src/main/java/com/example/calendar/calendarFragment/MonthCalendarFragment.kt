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
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.customView.DayEventAdapter
import com.example.calendar.customView.MonthDotDecorator
import com.example.calendar.customView.TodayDecorator
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.data.oldEvent.EventTable
import java.text.SimpleDateFormat
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens


class MonthCalendarFragment : MvpAppCompatFragment(),
    OpenNewEventView, CurrentDateView,
    ListEventView, MonthDotView,
    OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener {

    companion object {
        fun newInstance(): MonthCalendarFragment {
            return MonthCalendarFragment()
        }
    }

    // todo inject
    private val router = InjectApplication.inject.router

    @InjectPresenter
    lateinit var openNewEventPresenter: OpenNewEventPresenter

    @ProvidePresenter
    fun provideOpenNewEventPresenter(): OpenNewEventPresenter {
        return OpenNewEventPresenter(router)
    }

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
            v.rvEvents.context,
            linerLayoutManager.orientation
        )
        v.rvEvents.run {
            this.adapter = DayEventAdapter { _, position ->
                onClickEvent(position)
            }
            this.layoutManager = linerLayoutManager
            this.addItemDecoration(dividerItemDecoration)
        }

        return v
    }

    private fun onClickAdfAddNote() {
        if (v.cvMonthCalendar.selectedDate == null) {
            openNewEventPresenter.openOnTodayDay()
        } else {
            openNewEventPresenter.openOnDay(
                v.cvMonthCalendar.selectedDate.calendar
            )
        }
    }

    private fun onClickEvent(pos: Int) {
        val id = listEventPresenter.getId(pos)
        // todo need presenter ???
        router.navigateTo(Screens.EventScreen(id))
    }

    override fun onDateSelected(
        widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        currentDatePresenter.setCurrentDate(date.calendar)
    }

    override fun onDateLongClick(
        widget: MaterialCalendarView, date: CalendarDay) {
        openNewEventPresenter.openOnDay(date.calendar)
    }

    override fun onMonthChanged(
        widget: MaterialCalendarView, date: CalendarDay) {
        monthDotPresenter.onMonthChange(date.calendar)
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun setMonthDots(it: HashSet<Calendar>) {
        decorator.setDates(it)
        v.cvMonthCalendar.invalidateDecorators();
    }

    override fun setEvents(it: List<EventTable>) {
        v.rvEvents.adapter.run {
            (this as DayEventAdapter).setEvents(it, v.cvMonthCalendar.selectedDate.calendar)
        }
    }

    override fun setCurrentDate(date: Calendar) {
        v.cvMonthCalendar.selectedDate = CalendarDay.from(date)
        v.tvSelectDate.text = fmtCurDay.format(date.time)
        listEventPresenter.onDateSelected(date)
    }
}