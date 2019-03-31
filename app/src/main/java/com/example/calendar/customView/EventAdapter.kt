package com.example.calendar.customView

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.util.Log
import android.view.View
import com.example.calendar.R
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.inflate
import kotlinx.android.synthetic.main.view_event_day_calendar.view.*


class EventAdapter : RecyclerView.Adapter<EventAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }
    }

    private val events : ArrayList<EventTable>? = null

    fun setEvents(e : ArrayList<EventTable>) {
        events?.addAll(e)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = parent.inflate(R.layout.view_event_day_calendar, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (events != null) {
            holder.view.tvItemEventTitle.text = events[position].name
        } else {
            holder.view.tvItemEventTitle.text = "Error"
        }
    }

    override fun getItemCount() : Int {
        if (events != null)
            return events.size
        else
            return 0
    }
}