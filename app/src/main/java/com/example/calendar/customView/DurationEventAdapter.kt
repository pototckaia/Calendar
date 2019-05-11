package com.example.calendar.customView

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.data.oldEvent.EventTable
import com.example.calendar.helpers.*
import kotlinx.android.synthetic.main.view_duration_event_holder.view.*
import java.util.*


class DurationEventViewHolder(
    private val view: View,
    onClick: (View, Int) -> Unit
) :
    RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener { v -> onClick(v, adapterPosition) }
    }

    private val emptyTitle = "< Нет названия >"

    fun bind(e: EventTable, start: Calendar, end: Calendar) {
        if (e.name.isEmpty()) {
            view.tvEventTitle.text = emptyTitle
        } else {
            view.tvEventTitle.text = e.name
        }
        view.tvEventHourDuration.text = getDiff(e.started_at, e.ended_at, "HH:mm")
        if (start.eqDay(end)) {
            view.tvEventDay.height = 0
        } else {
            view.tvEventDay.text = getDayDiff(e.started_at, e.ended_at)
        }
    }
}

class DurationEventAdapter(
    private val onClickListener: (View, Int) -> Unit
) :
    RecyclerView.Adapter<DurationEventViewHolder>() {

    private val events = ArrayList<EventTable>()
    private val start = getCalendarWithDefaultTimeZone()
    private val end = getCalendarWithDefaultTimeZone()

    fun setEvents(d: List<EventTable>, s: Calendar, e: Calendar) {
        events.clear()
        events.addAll(d)
        start.timeInMillis = s.timeInMillis
        end.timeInMillis = e.timeInMillis
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DurationEventViewHolder{
        val view = parent.inflate(R.layout.view_duration_event_holder, false)
        return DurationEventViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: DurationEventViewHolder, position: Int) {
        val e = events[position]
        holder.bind(e, start, end)
    }

    override fun getItemCount(): Int {
        return events.size
    }
}