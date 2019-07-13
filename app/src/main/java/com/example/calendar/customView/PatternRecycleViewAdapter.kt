package com.example.calendar.customView

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.repository.server.model.PatternRequest
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_event_pattern_request.view.*


open class LifecycleOwnerViewHolder(containerView: View) :
    RecyclerView.ViewHolder(containerView), LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
    }

    fun markAttach() {
        // Lifecycle.State.CREATED doesn't work for this case
        // lifecycleRegistry.markState(Lifecycle.State.CREATED)
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        // lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    fun markDetach() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}

abstract class LifecycleOwnerRecycleAdapter<T : LifecycleOwnerViewHolder> : RecyclerView.Adapter<T>() {
    override fun onViewAttachedToWindow(holder: T) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: T) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}


class PatternRecycleViewAdapter(
    var patterns: ArrayList<PatternRequest>,
    val onRecurrenceRuleClick: (pos: Int, patter: PatternRequest) -> Unit,
    val onTimeZoneClick: (pos: Int) -> Unit
) : LifecycleOwnerRecycleAdapter<PatternRecycleViewAdapter.PatternViewHolder>() {

    inner class PatternViewHolder constructor(val v: EventPatternRequestView) : LifecycleOwnerViewHolder(v) {
        private var posItem = -1

        fun bind(item: PatternRequest, pos: Int) {
            posItem = pos

            v.viewModel.liveData.postValue(item)
            v.viewModel.liveData.observe(this, Observer {
                patterns[posItem] = it!!
            })

            v.tvNumber.text = (pos + 1).toString()
            v.setRecurrenceOnClick { onRecurrenceRuleClick(posItem, it) }
            v.setTimeZoneOnClick { onTimeZoneClick(posItem) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PatternViewHolder {
        val v = EventPatternRequestView(parent.context)
        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        v.layoutParams = lp

        return PatternViewHolder(v)
    }

    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        val pattern = patterns[position]

        val imgRemove = holder.v.ivDelete
        imgRemove.setOnClickListener {
            if (patterns.size <= 1) {
                Toast.makeText(it.context, "Должен быть покрайне мере один интервал времени", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            patterns.removeAt(position)

            notifyItemRemoved(position)
            for (i in position until patterns.size) {
                notifyItemChanged(i)
            }
        }
        holder.bind(pattern, position)
    }

    override fun getItemCount(): Int {
        return patterns.size
    }

    val maxItem = 20
    fun addItem(m: PatternRequest, context: Context) {
        if (patterns.size > maxItem) {
            Toast.makeText(context, "Больше создать нельзя", Toast.LENGTH_LONG).show()
            return
        }
        patterns.add(m)
        notifyItemInserted(patterns.size - 1)
    }

    fun updatePattern(m: PatternRequest, pos: Int) {
        patterns[pos] = m
        notifyItemChanged(pos)
    }

    fun updatePatterns(m: ArrayList<PatternRequest>) {
        patterns = m
        notifyDataSetChanged()
    }
}
