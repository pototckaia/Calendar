package com.example.calendar.customView

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.helpers.inflate
import kotlinx.android.synthetic.main.view_timezone_item.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


data class TimeZoneModel(
    val zoneId: ZoneId,
    val name: String
) {
    val offset: String
        get()  {
            val now = LocalDateTime.now()
            val zoneOffset = now.atZone(zoneId).offset.id.replace("Z", "+00:00")
            return String.format("UTC%s", zoneOffset)
        }

}


class TimeZoneRecycleViewAdapter(
    private val contactList: List<TimeZoneModel>,
    private val onSelected: (TimeZoneModel) -> Unit
) : RecyclerView.Adapter<TimeZoneRecycleViewAdapter.ZoneIdViewHolder>(), Filterable {

    private var contactListFiltered : List<TimeZoneModel> = contactList

    inner class ZoneIdViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                onSelected(contactListFiltered[adapterPosition]);
            }
        }
    }

    override fun getFilter() =
        object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = contactList
                } else {
                    val filteredList = ArrayList<TimeZoneModel>()
                    contactList.forEach {
                        if (it.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(it)
                        }
                    }
                    contactListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                contactListFiltered = filterResults.values as List<TimeZoneModel>
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneIdViewHolder {
        val itemView = parent.inflate(R.layout.view_timezone_item, false)
        return ZoneIdViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ZoneIdViewHolder, position: Int) {
        val z = contactListFiltered[position]
        holder.view.tvTitle.text = z.name
        holder.view.tvOffset.text = z.offset
    }

    override fun getItemCount() = contactListFiltered.size
}