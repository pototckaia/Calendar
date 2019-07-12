package com.example.calendar.customView

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.repository.server.model.PatternRequest
import kotlinx.android.synthetic.main.view_event_pattern_request.view.*


class PatternRecycleViewAdapter(
    var patterns: ArrayList<PatternRequest>,
    val onRecurrenceRuleClick: (pos: Int, patter: PatternRequest) -> Unit,
    val onTimeZoneClick: (pos: Int) -> Unit,
// todo make model view
    var getViewByPos : (Int) -> EventPatternRequestView
) : RecyclerView.Adapter<PatternRecycleViewAdapter.PatternViewHolder>() {

    inner class PatternViewHolder constructor(val v: EventPatternRequestView) : RecyclerView.ViewHolder(v) {
        private var posItem = -1

        fun bind(item: PatternRequest, pos: Int) {
            posItem = pos
            v.setPattern(item)

            v.tvNumber.text = (pos + 1).toString()
            v.etRecurrenceRule.inputType = InputType.TYPE_NULL
            v.etRecurrenceRule.setOnClickListener { onRecurrenceRuleClick(posItem, v.getPattern()) }
            v.etRecurrenceRule.setOnFocusChangeListener { _, b -> if (b) onRecurrenceRuleClick(posItem, v.getPattern()) }

            v.etTimezone.inputType = InputType.TYPE_NULL
            v.etTimezone.setOnClickListener { onTimeZoneClick(posItem) }
            v.etTimezone.setOnFocusChangeListener { _, b -> if (b) onTimeZoneClick(posItem) }
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
                patterns[i] = getViewByPos(i).getPattern()
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

    fun updateItem(m: PatternRequest, pos: Int) {
        patterns[pos] = m
        notifyItemChanged(pos)
    }

    fun setItem(m: ArrayList<PatternRequest>) {
        patterns = m
        notifyDataSetChanged()
    }
}
