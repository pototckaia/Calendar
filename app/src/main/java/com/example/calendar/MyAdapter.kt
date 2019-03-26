package com.example.calendar

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.util.Log
import android.view.View
import com.example.calendar.helpers.inflate
import kotlinx.android.synthetic.main.view_item_hour_day_calendar.view.*

class MyAdapter(internal val items: Array<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    var Header = arrayOf("")

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

        fun bind() {}
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        val view = parent.inflate(R.layout.view_item_hour_day_calendar, false)
        // set the view's size, margins, paddings and layout parameters
        //...
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    // If the list needs an update, call a notification method on the RecyclerView.Adapter object,
    //such as notifyItemChanged(). The layout manager then rebinds any affected view holders,
    //allowing their data to be updated.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.tvHour.text = items[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}