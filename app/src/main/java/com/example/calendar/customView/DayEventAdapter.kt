package com.example.calendar.customView

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.View
import com.example.calendar.R
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.*
import kotlinx.android.synthetic.main.view_event_day_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*


class DayEventViewHolder(
    private val view: View,
    onClick: (View, Int) -> Unit
) :
    RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener { v -> onClick(v, adapterPosition) }
    }

    private val fmtHour = SimpleDateFormat(
        "HH:mm", Locale.getDefault()
    )

    // todo hard text
    private val rangeOut = "---"
    private val allDay = "Весь день"

    fun bind(e: EventTable, day: Calendar) {
        view.tvEventTitle.text = e.name

        var first = fmtHour.format(e.started_at.time)
        var second = fmtHour.format(e.ended_at.time)

        if (e.started_at.lessDay(day) && e.ended_at.greaterDay(day)) {
            view.tvEventHourDuration.text = allDay
        } else {
            if (e.started_at.lessDay(day)) { first = rangeOut}
            if (e.ended_at.greaterDay(day)) { second = rangeOut }

            view.tvEventHourDuration.text = "${first} - ${second}"
        }

    }
}

class DayEventAdapter(
    private val onClickListener: (View, Int) -> Unit
) :
    RecyclerView.Adapter<DayEventViewHolder>() {

    private val events = ArrayList<EventTable>()
    private val dayEvent = getCalendarWithDefaultTimeZone()

    fun setEvents(d: List<EventTable>, day: Calendar) {
        events.clear()
        events.addAll(d)
        dayEvent.timeInMillis = day.timeInMillis
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayEventViewHolder {
        val view = parent.inflate(R.layout.view_event_day_calendar, false)
        return DayEventViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: DayEventViewHolder, position: Int) {
        val e = events[position]
        holder.bind(e, dayEvent)
    }

    override fun getItemCount(): Int {
        return events.size
    }
}