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
import com.example.calendar.repository.server.model.PatternRequest
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import kotlinx.android.synthetic.main.view_event_pattern_request.view.*
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


class CreateEventFragment : MvpAppCompatFragment(),
    CreateEventInfoView,
    DateClickView, RecurrenceEventView {

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
    lateinit var dateClickPresenter: DateClickPresenter

    @ProvidePresenter
    fun provideDateClickPresenter(): DateClickPresenter {
        // todo !!
        return DateClickPresenter(
            fromStringToZoned(arguments!!.getString(START_EVENT_KEY)!!),
            fromStringToZoned(arguments!!.getString(END_EVENT_KEY)!!),
            { d: ZonedDateTime -> validateStartEvent(d) },
            { true }
        )
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

    // todo inject
    private val router = InjectApplication.inject.router

    private lateinit var v: View
    private lateinit var pattern: View

    @InjectPresenter
    lateinit var recurrenceEventPresenter: RecurrenceEventPresenter

    lateinit var recurrenceViewModel: RecurrenceViewModel

    // todo error with clava
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

        recurrenceViewModel = activity?.run {
            ViewModelProviders.of(this).get(RecurrenceViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")

        recurrenceViewModel.recurrence.observe(this, Observer<String> { r ->
            recurrenceEventPresenter.onRuleChange(r)
        })

        pattern = v.vEventPatternRequest.v

        pattern.vBegin.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickBeginDay() }
        pattern.vBegin.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickBeginHour() }
        pattern.vEnd.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickEndDay() }
        pattern.vEnd.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickEndHour() }

        pattern.etRecurrenceRule.inputType = InputType.TYPE_NULL
        pattern.etRecurrenceRule.setOnClickListener { onRecurrenceRuleClick() }
        pattern.etRecurrenceRule.setOnFocusChangeListener { _, b -> if (b) onRecurrenceRuleClick() }

        v.etTimezone.setText(ZoneId.systemDefault().toString())

        return v
    }

    private fun initToolBar() {
        v.tbNoteCreate.setNavigationOnClickListener { router.exit() }
        v.tbNoteCreate.inflateMenu(R.menu.menu_enent_create)
        v.tbNoteCreate.menu.findItem(R.id.actionCreate).setOnMenuItemClickListener {
            //            onSave()
            true
        }
    }

    private fun onSave() {
        createEventPresenter.onSaveEvent(
            v.vEventRequest.getEventRequest(),
            getEventPatternRequest()
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
        pattern.vBegin.setDate(startLocal)
        pattern.vEnd.setDate(endLocal)
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
        pattern.etRecurrenceRule.setText(r)
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
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
            return startUTC < until
        }
        return true
    }

    fun getEventPatternRequest() =
        PatternRequest(
            started_at = dateClickPresenter.start,
            ended_at = dateClickPresenter.end,
            rrule = recurrenceEventPresenter.getRule(),
            // todo make exrules
            exrules = emptyList()
        )

}