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
import com.example.calendar.repository.db.EventInstance
import com.example.calendar.helpers.EVENT_INSTANCE_KEY
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import org.threeten.bp.ZonedDateTime
import androidx.appcompat.app.AlertDialog
import com.example.calendar.helpers.fromDateTimeUTC
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneOffset


enum class ModifyView {
    Update, Delete
}

enum class RecurrenceModifyViw(val pos: Int) {
    Future(0), All(1)
}

class EditEventFragment : MvpAppCompatFragment(),
    DateClickView, EditEventView, RecurrenceEventView {

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
            event.endedAtLocal,
            { d: ZonedDateTime -> validateStartEvent(d) },
            { true }
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
                recurrenceEventPresenter.getRule()
            )
        )
    }

    private fun onItemSelected(item: MenuItem?) {
        when (item?.itemId) {
            R.id.actionUpdate -> {
                onUpdateClick()
            }
            R.id.actionDelete -> {
                onDeleteClick()
            }
        }
    }

    override fun updateEventInfo(e: EventInstance) {
        v.etTextEvent.setText(e.nameEventRecurrence)
        dateClickPresenter.setDate(e.startedAtLocal, e.endedAtLocal)
        recurrenceEventPresenter.onRuleChange(e.rrule)
        v.tvTimeZone.text = e.zoneId.toString()

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

    private fun onUpdateClick() {
        if (editEventPresenter.isEventRecurrence()) {
            showChoice(ModifyView.Update)
            return
        }
        editEventPresenter.onUpdateAll(
            v.etTextEvent.text.toString(),
            "TODO",
            dateClickPresenter.start,
            dateClickPresenter.end,
            recurrenceEventPresenter.getRule()
        )
    }

    private fun onDeleteClick() {
        if (editEventPresenter.isEventRecurrence()) {
            showChoice(ModifyView.Delete)
            return
        }
        editEventPresenter.onDeleteAll()
    }

    private fun validateStartEvent(start: ZonedDateTime): Boolean {
        val rule = recurrenceEventPresenter.getRule()
        if (rule.isNotEmpty() && RecurrenceRule(rule).until != null) {
            val until = fromDateTimeUTC(RecurrenceRule(rule).until)
            val startUTC = start.withZoneSameInstant(ZoneOffset.UTC)
            if (startUTC >= until) {
                Toast
                    .makeText(
                        context,
                        "Дата начала события не может быть позже даты ДО в правиле переодичности",
                        Toast.LENGTH_SHORT)
                    .show()
            }
            return startUTC < until
        }
        return true
    }


    private fun showChoice(m: ModifyView) {
        val builder = AlertDialog.Builder(context!!)
        when (m) {
            ModifyView.Update -> builder.setTitle(R.string.title_choice_update)
            ModifyView.Delete -> builder.setTitle(R.string.title_choice_delete)
        }

        builder.setItems(
            R.array.choice_variance
        )
        { _, pos -> onRecurrenceModifyModeView(m, pos) }

        val dialog = builder.create()
        dialog.show()
    }

    private fun onRecurrenceModifyModeView(m: ModifyView, pos: Int) =
        when (pos) {
            RecurrenceModifyViw.Future.pos -> {
                when (m) {
                    ModifyView.Update ->
                        editEventPresenter.onUpdateFuture(
                            v.etTextEvent.text.toString(),
                            "TODO",
                            dateClickPresenter.start,
                            dateClickPresenter.end,
                            recurrenceEventPresenter.getRule()
                        )
                    ModifyView.Delete ->
                        editEventPresenter.onDeleteFuture()
                }
            }
            RecurrenceModifyViw.All.pos -> {
                when (m) {
                    ModifyView.Update ->
                        editEventPresenter.onUpdateAll(
                            v.etTextEvent.text.toString(),
                            "TODO",
                            dateClickPresenter.start,
                            dateClickPresenter.end,
                            recurrenceEventPresenter.getRule()
                        )
                    ModifyView.Delete ->
                        editEventPresenter.onDeleteAll()
                }
            }
            else -> {
            }
        }
}