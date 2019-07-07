package com.example.calendar.customView

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.helpers.*
import com.example.calendar.repository.server.model.EventInstance
import kotlinx.android.synthetic.main.view_duration_event_holder.view.*
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


class DurationEventViewHolder(
    private val view: View,
    onClick: (View, Int) -> Unit
) :
    RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener { v -> onClick(v, adapterPosition) }
    }

    private val emptyTitle = "< Нет названия >"

    fun bind(e: EventInstance, start: ZonedDateTime, end: ZonedDateTime) {
        if (e.entity.name.isEmpty()) {
            view.tvEventTitle.text = emptyTitle
        } else {
            view.tvEventTitle.text = e.entity.name
        }

        view.tvEventHourDuration.text = getStringDiff(e.started_at_local, e.ended_at_local, "HH:mm")
        if (isSameDay(start, end)) {
            view.tvEventDay.height = 0
        } else {
            view.tvEventDay.text = getStringDayDiff(e.started_at_local, e.ended_at_local)
        }
    }
}

class DurationEventAdapter(
    private val onClickListener: (View, Int) -> Unit
) :
    RecyclerView.Adapter<DurationEventViewHolder>() {

    private val events = ArrayList<EventInstance>()
    private var start = ZonedDateTime.now(ZoneId.systemDefault())
    private var end = ZonedDateTime.now(ZoneId.systemDefault())

    fun setEvents(d: List<EventInstance>, s: ZonedDateTime, e: ZonedDateTime) {
        events.clear()
        events.addAll(d)
        start = ZonedDateTime.from(s)
        end = ZonedDateTime.from(e)
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