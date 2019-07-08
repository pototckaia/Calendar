package com.example.calendar.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.calendar.R

class EventPatternRequestView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var v: View = LayoutInflater.from(context).inflate(
        R.layout.view_event_pattern_request, this, true
    )

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EventPatternRequestView)
        attr.recycle()
    }

}