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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.calendar.repository.db.EventInstance
import com.example.calendar.helpers.fromCalendar
import com.example.calendar.helpers.toCalendar
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


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
            InjectApplication.inject.repository
        )
    }

    @InjectPresenter
    lateinit var monthDotPresenter: MonthDotPresenter

    @ProvidePresenter
    fun provideMonthEventPresenter(): MonthDotPresenter {
        return MonthDotPresenter(
            // todo inject
            InjectApplication.inject.repository
        )
    }

    private lateinit var v: View

    private val decorator = MonthDotDecorator()
    private val fmtCurDay = DateTimeFormatter.ofPattern("dd/MM/yyyy")

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
                fromCalendar(v.cvMonthCalendar.selectedDate.calendar)
            )
        }
    }

    private fun onClickEvent(pos: Int) {
        val event = listEventPresenter.getEvent(pos)
        // todo need presenter ???
        router.navigateTo(Screens.EventScreen(event))
    }

    override fun onDateSelected(
        widget: MaterialCalendarView, date: CalendarDay, selected: Boolean
    ) {
        currentDatePresenter.setCurrentDate(fromCalendar(date.calendar))
    }

    override fun onDateLongClick(
        widget: MaterialCalendarView, date: CalendarDay
    ) {
        openNewEventPresenter.openOnDay(fromCalendar(date.calendar))
    }

    override fun onMonthChanged(
        widget: MaterialCalendarView, date: CalendarDay
    ) {
        monthDotPresenter.onMonthChange(fromCalendar(date.calendar))
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun setMonthDots(it: HashSet<ZonedDateTime>) {
        decorator.setDates(it)
        v.cvMonthCalendar.invalidateDecorators();
    }

    override fun setEvents(it: List<EventInstance>) {
        v.rvEvents.adapter.run {
            (this as DayEventAdapter).setEvents(
                it,
                fromCalendar(v.cvMonthCalendar.selectedDate.calendar)
            )
        }
    }

    override fun setCurrentDate(dateLocal: ZonedDateTime) {
        v.cvMonthCalendar.selectedDate = CalendarDay.from(toCalendar(dateLocal))
        v.tvSelectDate.text = dateLocal.format(fmtCurDay)
        listEventPresenter.onDateSelected(dateLocal)
    }
}