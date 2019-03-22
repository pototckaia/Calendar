package com.example.calendar;

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.calendar.data.Event
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_note_preview.view.*
import java.text.SimpleDateFormat
import java.util.*

class NoteReviewFragment  : Fragment() {
    private lateinit var event: Event
    private lateinit var notePreview: View
    private val formatter = SimpleDateFormat("EE, dd/MM HH:mm", Locale.getDefault())

    companion object {
        fun newInstance(): NoteReviewFragment{
            return NoteReviewFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
         notePreview = inflater.inflate(
            R.layout.fragment_note_preview,
            container, false
        )

        val defaultText = resources.getString(R.string.default_event_text)
        if (savedInstanceState == null) {
            val arg = arguments
            event = Event(defaultText)
            if (arg!= null && arg.containsKey(EVENT_KEY)) {
                event = arg.getParcelable(EVENT_KEY) ?: Event(defaultText)
            }
        } else {
            event = savedInstanceState.getParcelable(EVENT_KEY) ?: Event(defaultText)
        }

        updateInterface()

        return notePreview
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.clearFindViewByIdCache()
    }

    private fun updateInterface() {
        notePreview.etTextEvent.setText(event.text)
        notePreview.etBeginDate.setText(formatter.format(event.beginDate))
        notePreview.etEndDate.setText(formatter.format(event.endDate))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putParcelable(EVENT_KEY, event)
        }
        super.onSaveInstanceState(outState)
    }

}