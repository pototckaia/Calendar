package com.example.calendar.customView

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.example.calendar.R
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import kotlinx.android.synthetic.main.view_day_hour.view.*
import java.text.SimpleDateFormat
import java.util.*

class DayHourView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // TODO Local.getDefault and TimeZone.getDefault common ???
    private val fmt_day = SimpleDateFormat(
        "dd/MM/yyyy", Locale.getDefault()
    )

    private val fmt_hour = SimpleDateFormat(
        "HH:mm", Locale.getDefault()
    )

    private var v: View = LayoutInflater.from(context).inflate(
        R.layout.view_day_hour, this, true
    )

    var onDayClickListener: OnClickListener? = null
    var onHourClickListener: OnClickListener? = null

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.DayHourView)
        v.tvLabel.text = attr.getString(R.styleable.DayHourView_label_name)
        attr.recycle()

        v.etDay.setOnClickListener { v -> onClick(v, onDayClickListener) }
        v.etHour.setOnClickListener { v -> onClick(v, onHourClickListener) }
    }

    fun setDate(d: Calendar) {
        v.etDay.setText(fmt_day.format(d.time))
        v.etHour.setText(fmt_hour.format(d.time))
    }

    private fun onClick(v: View, listener: OnClickListener?) {
        if (listener != null) listener.onClick(v)
    }
}
