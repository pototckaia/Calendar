package com.example.calendar.customView

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.helpers.inflate
import com.example.calendar.repository.server.model.PermissionModel
import kotlinx.android.synthetic.main.view_permission.view.*

class PermissionRecycleViewAdapter(
    val permission: ArrayList<PermissionModel>,
    val onDeleteClick: (id: PermissionModel, pos: Int) -> Unit
) : RecyclerView.Adapter<PermissionRecycleViewAdapter.PermissionViewHolder>() {

    inner class PermissionViewHolder(val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(item: PermissionModel, pos: Int) {
            v.tvEntityName.text = item.entityName
            v.tvUser.text = "Предоставлено: ${item.username}"
            v.tvActionType.text = item.actionType.title
            v.ibDelete.setOnClickListener { onDeleteClick(item, pos) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val v = parent.inflate(R.layout.view_permission, false)
        return PermissionViewHolder(v)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        val p = permission[position]
        holder.bind(p, position)
    }

    override fun getItemCount() = permission.size

    fun removeByPos(p: Int) {
        permission.removeAt(p)
        notifyItemRemoved(p)
        for (i in p until permission.size) {
            notifyItemChanged(i)
        }
    }

    fun updateAll(m: ArrayList<PermissionModel>) {
        permission.clear()
        permission.addAll(m)
        notifyDataSetChanged()
    }
}
