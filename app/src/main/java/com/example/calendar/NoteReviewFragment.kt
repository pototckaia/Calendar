package com.example.calendar;

import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.Date

class NoteReviewFragment  : Fragment() {
    private lateinit var tvDateField: TextView
    private var date: Long = 0

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
        val view: View = inflater.inflate(
            R.layout.fragment_note_preview,
            container, false
        )
        tvDateField = view.findViewById(R.id.etTextEvent)
        getArguments().run {
            tvDateField.text = this?.getString(TEXT_VIEW_KEY)
        }
        return view
    }

    // This callback is called only when there is a saved instance that is previously saved by using
    // onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
    // other state here, possibly usable after onStart() has completed.
    // The savedInstanceState Bundle is same as the one used in onCreate().
//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        savedInstanceState?.run {
//            date = getLong(TEXT_VIEW_KEY)
//        }
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.run {
//            putLong(TEXT_VIEW_KEY, date)
//        }
//        super.onSaveInstanceState(outState)
//    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
//    override fun onStop() {
        // call the superclass method first
//        super.onStop()
//
        // save the note's current draft, because the activity is stopping
        // and we want to be sure the current note progress isn't lost.
//        val values = ContentValues().apply {
//            put(NotePad.Notes.COLUMN_NAME_NOTE, getCurrentNoteText())
//            put(NotePad.Notes.COLUMN_NAME_TITLE, getCurrentNoteTitle())
//        }
//
//         do this update in background on an AsyncQueryHandler or equivalent
//        asyncQueryHandler.startUpdate(
//            token,     // int token to correlate calls
//            null,      // cookie, not used here
//            uri,       // The URI for the note to update.
//            values,    // The map of column names and new values to apply to them.
//            null,      // No SELECT criteria are used.
//            null       // No WHERE columns are used.
//        )
//    }

}