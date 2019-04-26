package com.example.calendar.customView

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatDialogFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.calendarFragment.ListEventPresenter
import com.example.calendar.calendarFragment.ListEventView
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.END_LIST_EVENT_KEY
import com.example.calendar.helpers.START_LIST_EVENT_KEY
import kotlinx.android.synthetic.main.dialog_list_event.view.*
import java.util.Calendar

class ListEventDialog : MvpAppCompatDialogFragment(), ListEventView {

    companion object {
        fun newInstance(start: Calendar, end: Calendar) : ListEventDialog {
            val f = ListEventDialog()
            val bundle = Bundle()
            bundle.putLong(START_LIST_EVENT_KEY, start.timeInMillis)
            bundle.putLong(END_LIST_EVENT_KEY, end.timeInMillis)
            f.arguments = bundle
            return f
        }
    }

    @InjectPresenter
    lateinit var listEventPresenter: ListEventPresenter

    @ProvidePresenter
    fun provideListEventPresenter(): ListEventPresenter {
        return ListEventPresenter(
            // todo inject
            EventRoomDatabase.getInstance(context!!).eventDao(),
            arguments!!.getLong(START_LIST_EVENT_KEY),
            arguments!!.getLong(END_LIST_EVENT_KEY)
        )
    }


    lateinit var v: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val inflater = activity!!.layoutInflater
        v = inflater.inflate(R.layout.dialog_list_event, null)


        val linerLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            v.rvEventsMonthCalendar.context,
            linerLayoutManager.orientation
        )
        v.rvEventsMonthCalendar.run {
            // todo replace
            this.adapter = DayEventAdapter { _, position ->
                onClickEvent(position)
            }
            this.layoutManager = linerLayoutManager
            this.addItemDecoration(dividerItemDecoration)
        }

        return AlertDialog.Builder(activity)
            .setTitle("")
            .setView(v)
            .setCancelable(false)
            .create()
    }

    private fun onClickEvent(pos : Int) {}

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun setEvents(it: List<EventTable>) {
        // todo replace
        v.rvEventsMonthCalendar.adapter.run {
            (this as DayEventAdapter).setEvents(it, it[0].started_at)
        }
    }
}