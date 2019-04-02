package com.example.calendar.customView

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.example.calendar.R
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import kotlinx.android.synthetic.main.view_select_date.view.*
import java.text.SimpleDateFormat
import java.util.*

class SelectDateView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // TODO Local.getDefault and TimeZone.getDefault???
    private val fmt_day = SimpleDateFormat(
        "EE, dd/MM/yyyy", Locale.getDefault()
    )

    private val fmt_hour = SimpleDateFormat(
        "HH:mm", Locale.getDefault()
    )

    private var view: View = LayoutInflater.from(context).inflate(
        R.layout.view_select_date, this, true
    )

    var onClickDayListener: OnClickListener? = null
    var onClickHourListener: OnClickListener? = null


    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.SelectDateView)
        view.tvLabel.text = attr.getString(R.styleable.SelectDateView_label_name)
        attr.recycle()
        view.etDay.setOnClickListener { v -> onClick(v, onClickDayListener) }
        view.etHour.setOnClickListener { v -> onClick(v, onClickHourListener)}

    }

    var date: Calendar
        get() {
            // TODO: remove
            return getCalendarWithDefaultTimeZone()
        }
        set(value) {
            view.etDay.setText(fmt_day.format(value.time))
            view.etHour.setText(fmt_hour.format(value.time))
        }

    private fun onClick(v: View, listener: OnClickListener?) {
        if (listener != null)
            listener.onClick(v)
    }


}
