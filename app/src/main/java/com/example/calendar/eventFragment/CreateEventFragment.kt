package com.example.calendar.eventFragment;

import android.os.Bundle
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.helpers.*
import com.example.calendar.inject.InjectApplication
import kotlinx.android.synthetic.main.fragment_event.view.*
import org.threeten.bp.ZonedDateTime
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.calendar.repository.server.model.PatternRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.customView.PatternRecycleViewAdapter
import com.example.calendar.helpers.convert.fromStringToZoned
import com.example.calendar.helpers.convert.toStringFromZoned
import com.example.calendar.navigation.Screens
import org.threeten.bp.ZoneId


class ExitEventPatternViewModel : ViewModel() {
    var posItem : Int = -1

    val recurrence = MutableLiveData<String>()
    //    val location = MutableLiveData<String?>()
    val timezone = MutableLiveData<ZoneId>()

    fun isActivate() = posItem >= 0
    fun activate(i: Int = 0) { posItem = i }
    fun deactivate() { posItem = -1 }
}

class CreateEventFragment : MvpAppCompatFragment(),
    CreateEventView, PatternsSaveView {

    companion object {
        fun newInstance(
            startEvent: ZonedDateTime,
            endEvent: ZonedDateTime
        ): CreateEventFragment {
            val args = Bundle()
            args.run {
                putString(START_EVENT_KEY, toStringFromZoned(startEvent))
                putString(END_EVENT_KEY, toStringFromZoned(endEvent))
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
            router,
            InjectApplication.inject.repository
        )
    }

    @InjectPresenter
    lateinit var patternsPresenter: PatternsPresenter

    @ProvidePresenter
    fun providePatternsPresenter(): PatternsPresenter {
        val arg = arguments!!
        return PatternsPresenter(
            fromStringToZoned(arg.getString(START_EVENT_KEY)!!),
            fromStringToZoned(arg.getString(END_EVENT_KEY)!!)
        )
    }

    private val router = InjectApplication.inject.router

    private lateinit var v: View

    lateinit var exitViewModel: ExitEventPatternViewModel

    private val adapter: PatternRecycleViewAdapter
        get() = (v.rvPattern.adapter as PatternRecycleViewAdapter)

    // todo error with clava
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_event,
            container, false
        )
        initToolBar()

        exitViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExitEventPatternViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")

        exitViewModel.recurrence.observe(this, Observer { p ->
            onCloseRecurrenceSelect(p)
        })
        exitViewModel.timezone.observe(this, Observer { p ->
            onCloseTimezoneSelect(p)
        })

        val emptyPattern = patternsPresenter.patterns
        val adapter = PatternRecycleViewAdapter(emptyPattern, this::onRecurrenceRuleClick, this::onTimeZoneClick)
        val linerLayoutManager = LinearLayoutManager(context)
        v.rvPattern.run {
            this.adapter = adapter
            this.layoutManager = linerLayoutManager
        }

        v.fabAddPattern.setOnClickListener { addPattern(patternsPresenter.patternStub) }

        return v
    }

    private fun onCloseRecurrenceSelect(p: String) {
        if (exitViewModel.isActivate()) {
            patternsPresenter.onCloseRecurrenceSelect(p, exitViewModel.posItem)
            exitViewModel.deactivate()
        }
    }

    private fun onCloseTimezoneSelect(p: ZoneId) {
        if (exitViewModel.isActivate()) {
            patternsPresenter.onCloseTimezoneSelect(p, exitViewModel.posItem)
            exitViewModel.deactivate()
        }
    }

    private fun onRecurrenceRuleClick(posItem: Int, v: PatternRequest) {
        exitViewModel.activate(posItem)
        router.navigateTo(Screens.FreqScreen(v.startedAtTimezone, v.rrule))
    }

    private fun onTimeZoneClick(posItem: Int) {
        exitViewModel.activate(posItem)
        router.navigateTo(Screens.TimeZoneSelectScreen())
    }

    override fun updatePattern(m: PatternRequest, pos: Int) {
        adapter.updatePattern(m, pos)
    }

    fun addPattern(pattern: PatternRequest) {
        adapter.addItem(pattern, context!!)
    }

    override fun onStop() {
        patternsPresenter.onSaveInstanceState(adapter.patterns)
        super.onStop()
    }

    private fun initToolBar() {
        v.tbEventInstance.setNavigationOnClickListener { router.exit() }
        v.tbEventInstance.inflateMenu(R.menu.menu_enent_create)
        v.tbEventInstance.menu.findItem(R.id.actionCreate).setOnMenuItemClickListener {
            onSave()
            true
        }
    }

    private fun onSave() {
        createEventPresenter.onSaveEvent(v.vEventRequest.eventRequest, adapter.patterns)
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }
}