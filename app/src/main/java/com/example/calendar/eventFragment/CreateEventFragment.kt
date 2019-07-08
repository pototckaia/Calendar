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
    RecurrenceEventView {

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
        pattern = v.vEventPatternRequest.v

        v.vEventPatternRequest.init(mvpDelegate)
        initToolBar()

        if (savedInstanceState == null) {
            v.vEventPatternRequest.dateClickPresenter.setDate(
                fromStringToZoned(arguments!!.getString(START_EVENT_KEY)!!),
                fromStringToZoned(arguments!!.getString(END_EVENT_KEY)!!)
            )
        }

        recurrenceViewModel = activity?.run {
            ViewModelProviders.of(this).get(RecurrenceViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")

        recurrenceViewModel.recurrence.observe(this, Observer<String> { r ->
            recurrenceEventPresenter.onRuleChange(r)
        })

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
                v.vEventPatternRequest.dateClickPresenter.start,
                recurrenceEventPresenter.getRule()
            )
        )
    }

    override fun setRecurrenceViw(r: String) {
        pattern.etRecurrenceRule.setText(r)
    }

    override fun postRecurrence(r: String) {
        recurrenceViewModel.recurrence.postValue(r)
    }

    fun getEventPatternRequest() =
        PatternRequest(
            started_at = v.vEventPatternRequest.dateClickPresenter.start,
            ended_at = v.vEventPatternRequest.dateClickPresenter.end,
            rrule = recurrenceEventPresenter.getRule(),
            // todo make exrules
            exrules = emptyList()
        )

}