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
import com.example.calendar.data.EventInstance
import com.example.calendar.helpers.EVENT_INSTANCE_KEY
import com.example.calendar.inject.InjectApplication
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import org.threeten.bp.ZonedDateTime


class EditEventFragment : MvpAppCompatFragment(),
    DateClickView, EditEventView {

    companion object {
        fun newInstance(event: EventInstance): EditEventFragment {
            val args = Bundle()
            args.run {
                this.putParcelable(EVENT_INSTANCE_KEY, event)
            }
            val f = EditEventFragment()
            f.arguments = args
            return f
        }
    }

    @InjectPresenter
    lateinit var dateClickPresenter: DateClickPresenter

    @ProvidePresenter
    fun provideDateClickPresenter(): DateClickPresenter {
        val event = arguments!!.getParcelable<EventInstance>(EVENT_INSTANCE_KEY)
        return DateClickPresenter(
            event.startedAtLocal,
            event.endedAtLocal
        )
    }

    @InjectPresenter
    lateinit var editEventPresenter: EditEventPresenter

    @ProvidePresenter
    fun provideEditEventPresenter(): EditEventPresenter {
        return EditEventPresenter(
            // todo inject
            router,
            InjectApplication.inject.repository,
            arguments!!.getParcelable<EventInstance>(EVENT_INSTANCE_KEY)!!
        )
    }

    // todo inject
    private val router = InjectApplication.inject.router

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
        v.tbNoteCreate.setNavigationOnClickListener() { router.exit() }
        v.tbNoteCreate.inflateMenu(R.menu.menu_event_edit)
        v.tbNoteCreate.setOnMenuItemClickListener {
            onItemSelected(it);
            true
        }
    }

    private fun onItemSelected(item: MenuItem?) {
        when (item?.itemId) {
            R.id.actionUpdate -> {
//                editEventPresenter.onUpdate(
//                    view!!.etTextEvent.text.toString(),
//                    "TODO",
//                    dateClickPresenter.startLocal.withZoneSameInstant(ZoneOffset.UTC),
//                    dateClickPresenter.endLocal)
            }
            R.id.actionDelete -> {
//                editEventPresenter.onDelete()
            }
        }
    }



    override fun updateEventInfo(e: EventInstance) {
        v.etTextEvent.setText(e.nameEventRecurrence)
        dateClickPresenter.setDate(e.startedAtInstance, e.endedAtInstance)
    }

    override fun updateDateInfo(startLocal: ZonedDateTime, endLocal: ZonedDateTime) {
        v.vBegin.setDate(startLocal)
        v.vEnd.setDate(endLocal)
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    override fun showDatePickerDialog(local: ZonedDateTime, l: DatePickerDialog.OnDateSetListener) {
        val dpd = MaterialDatePickerDialog.newInstance(local, l)
        dpd.show(activity?.supportFragmentManager, "date-picker")
    }

    override fun showTimePickerDialog(local: ZonedDateTime, l: TimePickerDialog.OnTimeSetListener) {
        val tpd = TimePickerDialog(
            context, l,
            local.hour,
            local.minute,
            true
        )
        tpd.show()
    }
}