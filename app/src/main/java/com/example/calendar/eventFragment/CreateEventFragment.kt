package com.example.calendar.eventFragment;

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
import com.example.calendar.helpers.*
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


class CreateEventFragment : MvpAppCompatFragment(),
    CreateEventInfoView, DateClickView, RecurrenceEventView {

    companion object {
        fun newInstance(
            startEvent: ZonedDateTime,
            endEvent: ZonedDateTime
        ): CreateEventFragment {
            val args = Bundle()
            args.run {
                this.putString(START_EVENT_KEY, toStringFromZoned(startEvent))
                this.putString(END_EVENT_KEY, toStringFromZoned(endEvent))
            }
            val f = CreateEventFragment()
            f.arguments = args
            return f
        }
    }

    @InjectPresenter
    lateinit var createEventPresenter: CreateEventPresenter

    @ProvidePresenter
    fun provideCreateEventPresenter(): CreateEventPresenter {
        return CreateEventPresenter(
            // todo inject
            router,
            InjectApplication.inject.repository
        )
    }

    @InjectPresenter
    lateinit var dateClickPresenter: DateClickPresenter

    @ProvidePresenter
    fun provideDateClickPresenter(): DateClickPresenter {
        return DateClickPresenter(
            fromStringToZoned(arguments!!.getString(START_EVENT_KEY)),
            fromStringToZoned(arguments!!.getString(END_EVENT_KEY)),
            { d: ZonedDateTime -> validateStartEvent(d) },
            { true }
        )
    }

    @InjectPresenter
    lateinit var recurrenceEventPresenter: RecurrenceEventPresenter

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
        v.etRecurrenceRule.setOnFocusChangeListener { _, b -> if (b) onRecurrenceRuleClick() }

        v.tvTimeZone.text = ZoneId.systemDefault().toString()

        return v
    }

    private fun initToolBar() {
        v.tbNoteCreate.setNavigationOnClickListener { router.exit() }
        v.tbNoteCreate.inflateMenu(R.menu.menu_enent_create)
        v.tbNoteCreate.menu.findItem(R.id.actionCreate).setOnMenuItemClickListener {
            onSave()
            true
        }
    }

    private fun onSave() {
        createEventPresenter.onSaveEvent(
            view!!.etTextEvent.text.toString(),
            "TODO",
            dateClickPresenter.start,
            dateClickPresenter.end,
            recurrenceEventPresenter.getRule()
        )
    }

    private fun onRecurrenceRuleClick() {
        router.navigateTo(
            Screens.FreqScreen(
                dateClickPresenter.start,
                recurrenceEventPresenter.getRule()
            )
        )
    }

    override fun updateDateInfo(startLocal: ZonedDateTime, endLocal: ZonedDateTime) {
        v.vBegin.setDate(startLocal)
        v.vEnd.setDate(endLocal)
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

}