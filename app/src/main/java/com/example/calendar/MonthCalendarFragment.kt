package com.example.calendar

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
import com.example.calendar.view.OpenView
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.customView.EventAdapter
import com.example.calendar.customView.EventMonthDecorator
import com.example.calendar.customView.TodayDecorator
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.data.EventTable
import com.example.calendar.presenter.AbleAddEventPresenter
import com.example.calendar.presenter.ListEventPresenter
import com.example.calendar.presenter.MonthEventPresenter
import com.example.calendar.view.ListEventView
import com.example.calendar.view.MonthEventView
import java.time.Month
import java.util.*

class MonthCalendarFragment : MvpAppCompatFragment(),
    OpenView, ListEventView, MonthEventView,
    OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener {

    companion object {
        fun newInstance(): MonthCalendarFragment {
            return MonthCalendarFragment()
        }
    }

    @InjectPresenter
    lateinit var addEventPresenter: AbleAddEventPresenter

    @InjectPresenter
    lateinit var listEventPresenter: ListEventPresenter

    @ProvidePresenter
    fun provideListEventPresenter (): ListEventPresenter {
        return ListEventPresenter(
            EventRoomDatabase.getInstance(context!!).eventDao()
        )
    }

    @InjectPresenter
    lateinit var monthEventPresenter: MonthEventPresenter

    @ProvidePresenter
    fun provideMonthEventPresenter(): MonthEventPresenter {
        return MonthEventPresenter(
            EventRoomDatabase.getInstance(context!!).eventDao()
        )
    }

    private lateinit var v: View

    private val decorator = EventMonthDecorator()

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
            TodayDecorator(resources.getDrawable(R.drawable.today_circle_background, null))
        )

        v.abfAddNote.setOnClickListener() { onClickAdfAddNote() }

        v.rvEventsMonthCalendar.run {
            this.adapter = EventAdapter { _, position ->
                onClickEvent(position)
            }
            this.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }

        return v
    }

    private fun onClickAdfAddNote() {
        if (v.cvMonthCalendar.selectedDate == null) {
            addEventPresenter.addEventButtonClick()
        } else {
            addEventPresenter.addEventButtonClick(v.cvMonthCalendar.selectedDate.calendar)
        }
    }

    private fun onClickEvent(pos: Int) {
        listEventPresenter.openEvent(pos)
    }

    override fun onDateSelected(
        widget: MaterialCalendarView, date: CalendarDay,
        selected: Boolean
    ) {
        listEventPresenter.onDateSelected(date.calendar)
    }

    override fun onDateLongClick(widget: MaterialCalendarView, date: CalendarDay) {
        addEventPresenter.addEventButtonClick(date.calendar)
    }

    override fun onMonthChanged(widget: MaterialCalendarView, date: CalendarDay) {
        monthEventPresenter.onMonthChange(date.calendar)
    }

    override fun openFragment(f: androidx.fragment.app.Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.clMainContainer, f)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun showError(e: String) {
        Toast.makeText(context, "Something go wrong", Toast.LENGTH_SHORT).show()
    }

    override fun setMonthEvents(it: HashSet<Calendar>) {
        decorator.setDates(it)
        v.cvMonthCalendar.invalidateDecorators();
    }

    override fun setDayEvents(it: List<EventTable>) {
        v.rvEventsMonthCalendar.adapter.run {
            (this as EventAdapter).setEvents(it)
        }
    }

    override fun setCurrentDate(localStart: Calendar, localEnd: Calendar) {
        // common day
        v.cvMonthCalendar.selectedDate = CalendarDay.from(localStart)
    }
}