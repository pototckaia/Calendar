package com.example.calendar.customView

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.View
import com.example.calendar.R
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.inflate
import kotlinx.android.synthetic.main.view_event_day_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*


class EventViewHolder(
    private val view: View,
    onClick: (View, Int) -> Unit
) :
    RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener { v -> onClick(v, adapterPosition) }
    }

    fun bind(e: EventTable, fmt: SimpleDateFormat) {
        // todo update text
        val text = "${e.name}\n " +
                "${fmt.format(e.started_at.time)} - " +
                "${fmt.format(e.ended_at.time)} "

        view.tvItemEventTitle.text = text
    }
}

class EventAdapter(
    private val onClickListener: (View, Int) -> Unit
) :
    RecyclerView.Adapter<EventViewHolder>() {

    private val events = ArrayList<EventTable>()

    private val fmt = SimpleDateFormat(
        "dd/MM/yyyy HH:mm", Locale.getDefault()
    )

    fun setEvents(d: List<EventTable>) {
        events.clear()
        events.addAll(d)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val view = parent.inflate(R.layout.view_event_day_calendar, false)
        return EventViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val e = events[position]
        holder.bind(e, fmt)
    }

    override fun getItemCount(): Int {
        return events.size
    }
}