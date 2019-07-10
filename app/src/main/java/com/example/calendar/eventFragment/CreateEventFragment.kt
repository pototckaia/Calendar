package com.example.calendar.eventFragment;

import android.os.Bundle
import android.util.Log
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.helpers.*
import com.example.calendar.inject.InjectApplication
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import org.threeten.bp.ZonedDateTime
import com.example.calendar.customView.EventPatternRequestView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.calendar.repository.server.model.PatternRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.customView.PatternRecycleViewAdapter
import com.example.calendar.customView.PatternViewHolder
import kotlin.collections.ArrayList




class EventPatternViewModel: ViewModel() {
    val recurrence = MutableLiveData<String>()
    val recurrenceNew = MutableLiveData<Pair<Int, String>>()
    val location = MutableLiveData<Pair<Int, String>>()
    val timezone = MutableLiveData<Pair<Int, String>>()
}

class CreateEventFragment : MvpAppCompatFragment(),
    CreateEventInfoView, PatternListSaveView {

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
            // todo inject
            router,
            InjectApplication.inject.repository
        )
    }

    @InjectPresenter
    lateinit var patternListSavePresenter: PatternListSavePresenter

    @ProvidePresenter
    fun providePatternListPresenter(): PatternListSavePresenter {
        val arg = arguments!!
        return PatternListSavePresenter(
            fromStringToZoned(arg.getString(START_EVENT_KEY)!!),
            fromStringToZoned(arg.getString(END_EVENT_KEY)!!)
        )
    }

    // todo inject
    private val router = InjectApplication.inject.router

    private lateinit var v: View

    lateinit var recurrenceViewModel: EventPatternViewModel

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
        patternListSavePresenter.onCreateView()
        initToolBar()

        recurrenceViewModel = activity?.run {
            ViewModelProviders.of(this).get(EventPatternViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")

        recurrenceViewModel.recurrenceNew.observe(this, Observer<Pair<Int, String>> { p ->
            if (p.first >= 0) {
                // set and remove
                patternListSavePresenter.onRecurrenceExit(p.second, p.first)
                recurrenceViewModel.recurrenceNew.postValue(Pair(-1, ""))
            }
        })

        val emptyPattern = ArrayList<PatternRequest>()
        val linerLayoutManager = LinearLayoutManager(context)
        v.rvPattern.run {
            this.adapter = PatternRecycleViewAdapter(emptyPattern, {i : Int ->  v.rvPattern.getChildAt(i) as EventPatternRequestView})
            this.layoutManager = linerLayoutManager
        }

        v.btn.setOnClickListener { patternListSavePresenter.onAddPatterns() }

        return v
    }

    override fun updatePattern(m: PatternRequest, pos: Int) {
        (v.rvPattern.adapter as PatternRecycleViewAdapter).updateItem(m, pos)
    }

    override fun addPattern(pattern: PatternRequest) {
        (v.rvPattern.adapter as PatternRecycleViewAdapter).addItem(pattern, context!!)
    }

    override fun setPatterns(patterns: ArrayList<PatternRequest>) {
        Log.d("Event", patterns.toString(), null)
        Log.d("Event", patterns.size.toString(), null)
        (v.rvPattern.adapter as PatternRecycleViewAdapter).setItem(patterns)
    }

    override fun onStop() {
        Log.d("Event", getPatterns().size.toString(), null)
        patternListSavePresenter.onSaveInstanceState(getPatterns())
        super.onStop()
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
            v.vEventRequest.getEventRequest(),
            getPatterns()
        )
    }

    private fun getPatterns() : ArrayList<PatternRequest> {
        val patterns = ArrayList<PatternRequest>()
        val adapter = v.rvPattern.adapter!! as PatternRecycleViewAdapter
        for (index in 0 until adapter.itemCount) {
            val nextChild = v.rvPattern.findViewHolderForAdapterPosition(index)
            if (nextChild == null) {
                patterns.add(adapter.patterns[index])
            } else {
                val holder = nextChild as PatternViewHolder
                patterns.add(holder.v.getPattern())
            }
        }
        return patterns
    }
}