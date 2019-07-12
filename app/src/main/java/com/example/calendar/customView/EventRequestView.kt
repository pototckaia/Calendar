package com.example.calendar.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.calendar.R
import com.example.calendar.repository.server.model.EventRequest
import kotlinx.android.synthetic.main.view_event_request.view.*

class EventRequestView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var v: View = LayoutInflater.from(context).inflate(
        R.layout.view_event_request, this, true
    )

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EventRequestView)
//        v.tvLabel.text = attr.getString(R.styleable.DayHourView_label_name)
        attr.recycle()
    }

    fun getEventRequest() =
        EventRequest(
            name = v.etTextName.text.toString(),
            details = v.etTextDetails.text.toString(),
            location = v.etTextLocation.text.toString(),
            status = v.etTextStatus.text.toString()
        )
}
