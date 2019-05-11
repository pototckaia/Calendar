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
import com.example.calendar.data.oldEvent.EventTable
import com.example.calendar.helpers.*
import com.example.calendar.navigation.CiceroneApplication
import com.example.calendar.navigation.Screens
import kotlinx.android.synthetic.main.dialog_list_event.view.*
import java.util.*

class ListEventDialog : MvpAppCompatDialogFragment(),
    ListEventView {

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

    // todo inject
    private val router = CiceroneApplication.instance.router

    lateinit var v: View
    private val start = getCalendarWithDefaultTimeZone()
    private val end = getCalendarWithDefaultTimeZone()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val inflater = activity!!.layoutInflater
        v = inflater.inflate(R.layout.dialog_list_event, null)


        val linerLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            v.rvEvents.context,
            linerLayoutManager.orientation
        )
        // todo add dot
        v.rvEvents.run {
            this.adapter = DurationEventAdapter { _, position ->
                onClickEvent(position)
            }
            this.layoutManager = linerLayoutManager
            this.addItemDecoration(dividerItemDecoration)
        }

        val b = savedInstanceState ?: arguments!!

        start.timeInMillis = b.getLong(START_LIST_EVENT_KEY)
        end.timeInMillis = b.getLong(END_LIST_EVENT_KEY)
        setDuration(start, end)


        return AlertDialog.Builder(activity)
            .setTitle("")
            .setView(v)
            .setCancelable(false)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(START_LIST_EVENT_KEY, start.timeInMillis)
        outState.putLong(END_LIST_EVENT_KEY, end.timeInMillis)
    }

    private fun onClickEvent(pos : Int) {
        val id = listEventPresenter.getId(pos)
        router.navigateTo(Screens.EventScreen(id))
        dismiss()
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun setEvents(it: List<EventTable>) {
        v.rvEvents.adapter.run {
            (this as DurationEventAdapter).setEvents(it, start, end)
        }
    }

    private fun setDuration(start: Calendar, end: Calendar) {
        v.tvHour.text = getDiff(start, end, "HH:mm")
        v.tvDay.text = getDayDiff(start, end)
    }
}