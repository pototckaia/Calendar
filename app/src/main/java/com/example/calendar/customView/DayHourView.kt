package com.example.calendar.customView

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.calendar.R
import kotlinx.android.synthetic.main.view_day_hour.view.*
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class DayHourView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val fmt_day = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val fmt_hour = DateTimeFormatter.ofPattern("HH:mm")

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

    fun setDate(d: ZonedDateTime) {
        v.etDay.setText(d.format(fmt_day))
        v.etHour.setText(d.format(fmt_hour))
    }

    private fun onClick(v: View, listener: OnClickListener?) {
        if (listener != null) listener.onClick(v)
    }
}
