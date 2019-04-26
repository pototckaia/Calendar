package com.example.calendar.customView

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.calendar.R

class ListEventDialog : AppCompatDialogFragment() {

    companion object {
        fun newInstance() : ListEventDialog {
            val f = ListEventDialog()
            val bundle = Bundle()
            // put something
            f.arguments = bundle
            return f
        }
    }

    lateinit var v: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val inflater = activity!!.layoutInflater
        v = inflater.inflate(R.layout.dialog_list_event, null)

        return AlertDialog.Builder(activity)
            .setTitle("")
            .setView(v)
            .setCancelable(false)
            .create()
    }
}