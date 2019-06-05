package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.RecurrenceViewModel
import com.example.calendar.customView.MaterialDatePickerDialog
import com.example.calendar.data.EventInstance
import com.example.calendar.helpers.EVENT_INSTANCE_KEY
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import org.threeten.bp.ZonedDateTime


class EditEventFragment : MvpAppCompatFragment(),
    DateClickView, EditEventView, RecurrenceEventView  {

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

    @InjectPresenter
    lateinit var recurrenceEventPresenter: RecurrenceEventPresenter

    @ProvidePresenter
    fun provideRecurrenceEventPresenter(): RecurrenceEventPresenter {
        val e = arguments!!.getParcelable<EventInstance>(EVENT_INSTANCE_KEY)!!
        return RecurrenceEventPresenter(e.rrule)
    }


    // todo inject
    private val router = InjectApplication.inject.router

    private lateinit var v: View
    lateinit var recurrenceViewModel: RecurrenceViewModel

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

        recurrenceViewModel = activity?.run {
            ViewModelProviders.of(this).get(RecurrenceViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")

        recurrenceViewModel.recurrence.observe(this, Observer<String> { r ->
            recurrenceEventPresenter.onRuleChange(r)
        })

        initToolBar()

        v.vBegin.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickBeginDay() }
        v.vBegin.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickBeginHour() }
        v.vEnd.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickEndDay() }
        v.vEnd.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickEndHour() }

        v.etRecurrenceRule.inputType = InputType.TYPE_NULL
        v.etRecurrenceRule.setOnClickListener { onRecurrenceRuleClick() }
        v.etRecurrenceRule.setOnFocusChangeListener { view, b -> if (b) onRecurrenceRuleClick() }

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

    private fun onRecurrenceRuleClick() {
        router.navigateTo(
            Screens.FreqScreen(
                dateClickPresenter.start,
                recurrenceEventPresenter.getRule()))
    }

    private fun onItemSelected(item: MenuItem?) {
        when (item?.itemId) {
            R.id.actionUpdate -> {
                editEventPresenter.onUpdate(
                    v.etTextEvent.text.toString(),
                    "TODO",
                    dateClickPresenter.start,
                    dateClickPresenter.end,
                    recurrenceEventPresenter.getRule())
            }
            R.id.actionDelete -> {
                editEventPresenter.onDelete()
            }
        }
    }



    override fun updateEventInfo(e: EventInstance) {
        v.etTextEvent.setText(e.nameEventRecurrence)
        dateClickPresenter.setDate(e.startedAtInstance, e.endedAtInstance)
        recurrenceEventPresenter.onRuleChange(e.rrule)
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

    override fun setRecurrenceViw(r: String) {
        v.etRecurrenceRule.setText(r)
    }

    override fun postRecurrence(r: String) {
        recurrenceViewModel.recurrence.postValue(r)
    }
}