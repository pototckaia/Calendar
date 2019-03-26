package com.example.calendar

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_month_calendar.view.*
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.example.calendar.view.AddEventView
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_month_calendar.*

class MonthCalendarFragment : MvpAppCompatFragment(), AddEventView,
    OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener {

    companion object {
        fun newInstance(): MonthCalendarFragment {
            return MonthCalendarFragment()
        }
    }

    @InjectPresenter
    lateinit var addEventPresenter: AddEventPresenter

    private lateinit var v: View

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
        v.abfAddNote.setOnClickListener() { onClickAdfAddNote() }

        return v
    }

    private fun onClickAdfAddNote() {
        addEventPresenter.addEventButtonClick(v.cvMonthCalendar.selectedDate.calendar)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        this.clearFindViewByIdCache()
//    }

    override fun onDateSelected(
        widget: MaterialCalendarView, date: CalendarDay,
        selected: Boolean
    ) {}

    override fun onDateLongClick(widget: MaterialCalendarView, date: CalendarDay) {}

    override fun onMonthChanged(widget: MaterialCalendarView, date: CalendarDay) {}

    override fun openFragment(f: Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.clMainContainer, f)
            ?.addToBackStack(null)
            ?.commit()
    }
}