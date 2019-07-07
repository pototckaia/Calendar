package com.example.calendar.customView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.calendar.R
import kotlinx.android.synthetic.main.view_week.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.helpers.inflate
import android.widget.TextView
import com.example.calendar.eventFragment.eWeekView
import org.dmfs.rfc5545.recur.RecurrenceRule

class ItemSelected(val text: String) {
    var isSelected = true
}

class SelectedStringViewHolder constructor(private val v: View) : RecyclerView.ViewHolder(v) {
    fun bind(item: ItemSelected, allItem: List<ItemSelected>) {
        // todo rename id
        val t = v.findViewById<TextView>(R.id.text1)
        t.text = item.text
        setBackground(item)

        t.setOnClickListener {
            val allSelected = allItem.filter { it.isSelected }
            if (item.isSelected && allSelected.size == 1) return@setOnClickListener

            item.isSelected = !item.isSelected
            setBackground(item)
        }
    }

    private fun setBackground(item: ItemSelected) {
        // todo color
        v.setBackgroundColor(if (item.isSelected) Color.GRAY else Color.TRANSPARENT)
    }
}

class SelectedStringAdapter(
    week_: Array<String>
) : RecyclerView.Adapter<SelectedStringViewHolder>() {

    private val week = week_.map { ItemSelected(it) }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedStringViewHolder {
        val view = parent.inflate(R.layout.selectable_list_item, false)
        return SelectedStringViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedStringViewHolder, position: Int) {
        val text = week[position]
        holder.bind(text, week)
    }

    override fun getItemCount(): Int {
        return week.size
    }

    fun getSelectedString() : List<String> {
        return week
            .filter { it.isSelected }
            .map { it.text }
    }

    fun getSelectedPos() : List<Int> {
        return week
            .map { Pair<ItemSelected, Int>(it, week.indexOf(it)) }
            .filter { it.first.isSelected }
            .map { it.second }
    }

    fun setSelectedPos(i: List<Int>) {
        week.forEach { it.isSelected = false }
        i.forEach { week[it].isSelected = true }
        notifyDataSetChanged()
    }
}

class WeekView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private var v: View = LayoutInflater.from(context).inflate(
        R.layout.view_week, this, true
    )

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.WeekView)
        //v.tvLabel.text = attr.getString(R.styleable.DayHourView_label_name)
        attr.recycle()

        val horizontalLayoutManagaer = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        v.lvWeek.run {
            layoutManager = horizontalLayoutManagaer
            adapter = SelectedStringAdapter(resources.getStringArray(R.array.week))
            setHasFixedSize(true)
        }
    }

    private fun getAdapter() : SelectedStringAdapter =
        (v.lvWeek.adapter as SelectedStringAdapter)


    fun getSelected() : List<RecurrenceRule.WeekdayNum> {
        val s = getAdapter().getSelectedPos()
        // todo !!
        return s.map { eWeekView.fromPos(it)!!.toWeekNum() }
    }

    fun setSelected(s: List<RecurrenceRule.WeekdayNum>) {
        // todo !!
        val u = s.map { eWeekView.fromWeekNum(it)!!.pos }
        getAdapter().setSelectedPos(u)
    }

    fun setSelected(s: eWeekView) {
        getAdapter().setSelectedPos(listOf(s.pos))
    }


}
