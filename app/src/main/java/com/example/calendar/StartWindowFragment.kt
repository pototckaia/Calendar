package com.example.calendar

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.view.View.OnClickListener
import android.widget.Toast
import java.util.*

class StartWindowFragment : Fragment(), OnDateChangeListener, OnClickListener {
    lateinit var calendarView: CalendarView
    lateinit var abfAddNote: FloatingActionButton

    private var mListener: clickListener? = null

    interface clickListener {
        fun onClickAddNote(date: Date)
    }


    companion object {
        fun newInstance(): StartWindowFragment {
            return StartWindowFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(
            R.layout.fragment_start_window,
            container, false
        )
        calendarView = view.findViewById(R.id.calendarMonthView)
        calendarView.setOnDateChangeListener(this)
        abfAddNote = view.findViewById(R.id.abfAddNote)
        abfAddNote.setOnClickListener(this)

        return view
    }

    override fun onSelectedDayChange(
        view: CalendarView,
        year: Int, month: Int, dayOfMonth: Int
    ) {
        val msg = "Selected date is ${dayOfMonth}/${month + 1}/${year}"
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        if (mListener != null) {
            mListener!!.onClickAddNote(Date(calendarView.date))
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is clickListener) {
            mListener = context
        } else {
            throw RuntimeException("${context!!.toString()} must implement OnFragmentInteractionListener")
        }
    }
}