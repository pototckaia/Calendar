package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.customView.MaterialDatePickerDialog
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.EVENT_ID_KEY
import com.example.calendar.remove.BackPressedPresenter
import com.example.calendar.remove.BackPressedView
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import java.util.Calendar


class EditEventFragment : MvpAppCompatFragment(),
    BackPressedView, DateClickView,
    EditEventView {

    companion object {
        fun newInstance(id: String): EditEventFragment {
            val args = Bundle()
            args.run {
                this.putString(EVENT_ID_KEY, id)
            }
            val f = EditEventFragment()
            f.arguments = args
            return f
        }
    }

    @InjectPresenter
    lateinit var backPressedPresenter: BackPressedPresenter

    @InjectPresenter
    lateinit var dateClickPresenter: DateClickPresenter

    @InjectPresenter
    lateinit var editEventPresenter: EditEventPresenter

    @ProvidePresenter
    fun provideEditEventPresenter(): EditEventPresenter {
        return EditEventPresenter(
            // todo inject
            EventRoomDatabase.getInstance(context!!).eventDao(),
            arguments!!.getString(EVENT_ID_KEY)!!
        )
    }


    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_create_event,
            container, false
        )

        initToolBar()

        v.vBegin.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickBeginDay() }
        v.vBegin.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickBeginHour() }
        v.vEnd.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickEndDay() }
        v.vEnd.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickEndHour() }

        return v
    }

    private fun initToolBar() {
        v.tbNoteCreate.setNavigationOnClickListener() { backPressedPresenter.onBackPressed() }
        v.tbNoteCreate.inflateMenu(R.menu.menu_event_edit)
        v.tbNoteCreate.setOnMenuItemClickListener {
            onItemSelected(it);
            true
        }
    }

    private fun onItemSelected(item: MenuItem?) {
        when (item?.itemId) {
            R.id.actionUpdate -> {
                editEventPresenter.onUpdate(
                    view!!.etTextEvent.text.toString(),
                    dateClickPresenter.startEvent,
                    dateClickPresenter.endEvent)
            }
            R.id.actionDelete -> {
                editEventPresenter.onDelete(
                    backPressedPresenter)
            }
        }
    }



    override fun updateEventInfo(e: EventTable) {
        v.etTextEvent.setText(e.name)
        dateClickPresenter.setDate(e.started_at, e.ended_at)
    }

    override fun updateDateInfo(begin: Calendar, end: Calendar) {
        v.vBegin.setDate(begin)
        v.vEnd.setDate(end)
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun showDatePickerDialog(c: Calendar, l: DatePickerDialog.OnDateSetListener) {
        val dpd = MaterialDatePickerDialog.newInstance(c, l)
        dpd.show(activity?.supportFragmentManager, "date-picker")
    }

    override fun showTimePickerDialog(c: Calendar, l: TimePickerDialog.OnTimeSetListener) {
        val tpd = TimePickerDialog(
            context, l,
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            true
        )
        tpd.show()
    }

    override fun finishView() {
        activity!!.supportFragmentManager.popBackStack()
    }

}