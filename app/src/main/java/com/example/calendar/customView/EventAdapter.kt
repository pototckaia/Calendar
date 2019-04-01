package com.example.calendar.customView

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.util.Log
import android.view.View
import com.example.calendar.R
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.inflate
import kotlinx.android.synthetic.main.view_event_day_calendar.view.*


// todo replace to ArrayList
class EventAdapter(private val events: List<EventTable>, private val listener: (View, Int) -> Unit) :
    RecyclerView.Adapter<EventAdapter.MyViewHolder>() {

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
            holder.view.tvItemEventTitle.text = events[position].name
    }

    override fun getItemCount() : Int {
        return events.size
    }
}