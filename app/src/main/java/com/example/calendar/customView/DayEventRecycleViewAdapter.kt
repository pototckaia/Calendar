package com.example.calendar.customView

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.View
import com.example.calendar.R
import com.example.calendar.helpers.*
import com.example.calendar.repository.server.model.EventInstance
import kotlinx.android.synthetic.main.view_day_event_holder.view.*
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


class DayEventViewHolder(
    private val view: View,
    onClick: (View, Int) -> Unit
) :
    RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener { v -> onClick(v, adapterPosition) }
    }

    private val fmtHour =  DateTimeFormatter.ofPattern("HH:mm")

    // todo hard text
    private val rangeOut = "---"
    private val allDay = "Весь день"
    private val emptyTitle = "< Нет названия >"

    fun bind(e: EventInstance, day: ZonedDateTime) {
        if (e.entity.name.isEmpty()) {
            view.tvEventTitle.text = emptyTitle
        } else {
            view.tvEventTitle.text = e.entity.name
        }

        var first = e.started_at_local.format(fmtHour)
        var second = e.started_at_local.format(fmtHour)

        if (lessDay(e.started_at_local, day) && moreDay(e.ended_at_local, day)) {
            view.tvEventHourDuration.text = allDay
        } else {
            if (lessDay(e.started_at_local, day)) { first = rangeOut}
            if (moreDay(e.ended_at_local, day)) { second = rangeOut }

            view.tvEventHourDuration.text = "$first - $second"
        }

    }
}

class DayEventRecycleViewAdapter(
    private val onClickListener: (View, Int) -> Unit
) :
    RecyclerView.Adapter<DayEventViewHolder>() {

    private val events = ArrayList<EventInstance>()
    private var dayEvent = ZonedDateTime.now(ZoneId.systemDefault())

    fun setEvents(d: List<EventInstance>, day: ZonedDateTime) {
        events.clear()
        events.addAll(d)
        dayEvent = ZonedDateTime.from(day)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayEventViewHolder {
        val view = parent.inflate(R.layout.view_day_event_holder, false)
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