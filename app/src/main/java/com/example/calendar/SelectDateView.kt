package com.example.calendar

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.view_select_date.view.*
import java.text.SimpleDateFormat
import java.util.*

class SelectDateView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val fmt_day = SimpleDateFormat(
        "EE, dd/MM/yyyy", Locale.getDefault()
    )

    private val fmt_hour = SimpleDateFormat(
        "HH:mm", Locale.getDefault()
    )


    private var view: View = LayoutInflater.from(context).inflate(
        R.layout.view_select_date, this, true
    )

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.SelectDateView)
        view.tvLabel.text = attr.getString(R.styleable.SelectDateView_label_name)
        attr.recycle()
    }
}
