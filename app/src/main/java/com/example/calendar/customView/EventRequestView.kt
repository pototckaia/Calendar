package com.example.calendar.customView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.calendar.R
import com.example.calendar.repository.server.model.EventRequest
import kotlinx.android.synthetic.main.view_event_request.view.*

class EventRequestView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_event_request, this)
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EventRequestView)
        attr.recycle()
    }

    var eventRequest: EventRequest
        get() = EventRequest(
            name = etTextName.text.toString(),
            details = etTextDetails.text.toString(),
            location = etTextLocation.text.toString(),
            status = etTextStatus.text.toString()
        )
        set(value) {
            etTextName.setText(value.name)
            etTextDetails.setText(value.details)
            etTextLocation.setText(value.location)
            etTextStatus.setText(value.status)
        }

}
