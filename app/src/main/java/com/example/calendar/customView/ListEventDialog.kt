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
import com.example.calendar.helpers.*
import com.example.calendar.helpers.convert.fromStringToZoned
import com.example.calendar.helpers.convert.toStringFromZoned
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import com.example.calendar.repository.server.model.EventInstance
import kotlinx.android.synthetic.main.dialog_list_event.view.*
import org.threeten.bp.ZonedDateTime


class ListEventDialog : MvpAppCompatDialogFragment(),
    ListEventView {

    companion object {
        fun newInstance(start: ZonedDateTime, end: ZonedDateTime) : ListEventDialog {
            val f = ListEventDialog()
            val bundle = Bundle()
            bundle.putString(START_LIST_EVENT_KEY, toStringFromZoned(start))
            bundle.putString(END_LIST_EVENT_KEY, toStringFromZoned(end))
            f.arguments = bundle
            return f
        }
    }

    @InjectPresenter
    lateinit var listEventPresenter: ListEventPresenter

    @ProvidePresenter
    fun provideListEventPresenter(): ListEventPresenter {
        val startString = arguments!!.getString(START_LIST_EVENT_KEY)!!
        val endString = arguments!!.getString(END_LIST_EVENT_KEY)!!
        return ListEventPresenter(
            InjectApplication.inject.repository,
            fromStringToZoned(startString),
            fromStringToZoned(endString)
        )
    }

    private val router = InjectApplication.inject.router

    lateinit var v: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val inflater = activity!!.layoutInflater
        v = inflater.inflate(R.layout.dialog_list_event, null)

        val linerLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            v.rvEvents.context,
            linerLayoutManager.orientation
        )
        // todo add dot view
        v.rvEvents.run {
            this.adapter = DurationEventRecycleViewAdapter { _, position ->
                onClickEvent(position)
            }
            this.layoutManager = linerLayoutManager
            this.addItemDecoration(dividerItemDecoration)
        }

        setDuration(listEventPresenter.start, listEventPresenter.end)


        return AlertDialog.Builder(activity)
            .setTitle("")
            .setView(v)
            .setCancelable(false)
            .create()
    }

    private fun onClickEvent(pos : Int) {
        val event = listEventPresenter.getEvent(pos)
        router.navigateTo(Screens.EventScreen(event))
        dismiss()
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun setEvents(it: List<EventInstance>) {
        v.rvEvents.adapter.run {
            (this as DurationEventRecycleViewAdapter)
                .setEvents(it, listEventPresenter.start, listEventPresenter.end)
        }
    }

    private fun setDuration(start: ZonedDateTime, end: ZonedDateTime) {
        v.tvHour.text = getStringDiff(start, end, "HH:mm")
        v.tvDay.text = getStringDayDiff(start, end)
    }
}