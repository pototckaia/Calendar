package com.example.calendar

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_day_calendar.*
import kotlinx.android.synthetic.main.fragment_day_calendar.view.*
import java.text.SimpleDateFormat


class DayCalendarFragment : Fragment() {

    companion object {
        fun newInstance(): DayCalendarFragment {
            return DayCalendarFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(
            R.layout.fragment_day_calendar,
            container, false
        )
        view.abfAddNote.setOnClickListener() { onClickAddNote() }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.clearFindViewByIdCache()
    }

    fun onClickAddNote() {
        val formatter = SimpleDateFormat("EE, dd MMM YYYY")
        val text = formatter.format(cdvMu.curDate.time)

        val bundle = Bundle()
        bundle.putString(TEXT_VIEW_KEY, text)
        val fragment = NoteReviewFragment.newInstance()
        fragment.arguments = bundle
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.clMainContainer,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }
}