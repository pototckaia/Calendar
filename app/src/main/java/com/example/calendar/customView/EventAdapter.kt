package com.example.calendar.customView

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.util.Log
import android.view.View
import com.example.calendar.R
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.inflate
import kotlinx.android.synthetic.main.view_event_day_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*


// todo replace to ArrayList
class EventAdapter(private val listener: (View, Int) -> Unit) :
    RecyclerView.Adapter<EventAdapter.MyViewHolder>() {

    private val events = ArrayList<EventTable>()
    private val fmt_day = SimpleDateFormat(
    "EE, dd/MM/yyyy HH:mm", Locale.getDefault()
    )

    fun setEvents(d: List<EventTable>) {
        events.clear()
        events.addAll(d)
        notifyDataSetChanged()
    }


    class MyViewHolder(val view: View, private val listener: (View, Int) -> Unit) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)

        }

        override fun onClick(v: View) {
            listener(v, adapterPosition)
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = parent.inflate(R.layout.view_event_day_calendar, false)
        return MyViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val text = "${events[position].name}\n " +
                "${fmt_day.format(events[position].started_at.time)} - " +
                "${fmt_day.format(events[position].ended_at.time)} "
        holder.view.tvItemEventTitle.text = text
    }

    override fun getItemCount() : Int {
        return events.size
    }
}