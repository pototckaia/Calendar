package com.example.calendar.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.calendar.R
import com.example.calendar.helpers.inflate
import kotlinx.android.synthetic.main.my_text_view.view.*

class MyAdapter(internal val items: Array<String>,
                val context: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val tvItemDate = view.itemDate

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        fun bind() {}
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(context).inflate(R.layout.my_text_view, parent, false)
//        val textView = parent.inflate(R.layout.my_text_view, false) as TextView
        // set the view's size, margins, paddings and layout parameters
        //...
        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
//    If the list needs an update, call a notification method on the RecyclerView.Adapter object,
//    such as notifyItemChanged(). The layout manager then rebinds any affected view holders,
//    allowing their data to be updated.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tvItemDate.text = items[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}