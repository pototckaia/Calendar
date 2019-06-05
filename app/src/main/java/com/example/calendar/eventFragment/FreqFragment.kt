package com.example.calendar.eventFragment

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.*
import com.example.calendar.customView.MaterialDatePickerDialog
import com.example.calendar.helpers.*
import com.example.calendar.inject.InjectApplication
import kotlinx.android.synthetic.main.fragment_freq.view.*
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class FreqFragment: MvpAppCompatFragment(),
    RecurrenceRuleView, OnBackPressed {

    companion object {
        fun newInstance(start: ZonedDateTime, freq: String = "") : FreqFragment {
            val args = Bundle()
            args.run {
                this.putString(RULE_RECURRENCE_RULE, freq)
                this.putString(START_RECURRENCE_RULE, toStringFromZoned(start))
            }

            val f = FreqFragment()
            f.arguments = args
            return f
        }
    }

    @InjectPresenter
    lateinit var freqPresenter: FreqPresenter

    @ProvidePresenter
    fun provideFreqPresenter(): FreqPresenter {
        val args = arguments!!
        return FreqPresenter(
            args.getString(RULE_RECURRENCE_RULE)!!,
            fromStringToZoned(args.getString(START_RECURRENCE_RULE)!!)
        )
    }

    lateinit var v: View
    lateinit var recurrenceViewModel: RecurrenceViewModel
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // todo month, week, year
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_freq,
            container, false
        )
        recurrenceViewModel = activity?.run {
            ViewModelProviders.of(this).get(RecurrenceViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")



        v.tbFreqFragment.setNavigationOnClickListener { freqPresenter.onBack() }
        v.spFreq.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long)
                    = selectedFreq(selectedItemPosition)

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })
        v.spDuration.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long)
                    = selectedDuration(selectedItemPosition)

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })
        v.tvDate.setOnClickListener { freqPresenter.onUntilClick() }

        return v
    }

    override fun onBackPressed() {
        freqPresenter.onBack()
    }

    private fun selectedFreq(selectedItemPosition: Int) {
        setEnableGroup(selectedItemPosition != FreqView.NEVER.pos)
        freqPresenter.onSelectItemFreq(selectedItemPosition)
    }

    private fun selectedDuration(selectedItemPosition: Int) {
        v.etCount.visibility = getVisibility(selectedItemPosition == DurationView.COUNT.pos)
        if (selectedItemPosition != DurationView.COUNT.pos) {
            val v : View = view?.rootView ?: View(context)
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }

        v.tvDate.visibility = getVisibility(selectedItemPosition == DurationView.UNTIL.pos)
    }

    private fun setEnableGroup(enable: Boolean) {
        v.etEach.isEnabled = enable
        v.spDuration.isEnabled = enable
        v.tvDate.isEnabled = enable
        v.etCount.isEnabled = enable
    }

    private fun getVisibility(b: Boolean)
            =  if (b) View.VISIBLE else View.INVISIBLE



    override fun openCalendar(until: ZonedDateTime, l: DatePickerDialog.OnDateSetListener) {
        val dpd = MaterialDatePickerDialog.newInstance(until, l)
        dpd.show(activity?.supportFragmentManager, "date-picker")
    }


    override fun setViewRule(it: RecurrenceRule) {
        v.spFreq.setSelection(FreqView.fromFreq(it.freq)!!.pos)

        v.etEach.setText(it.interval.toString())

        if (it.count != null) {
            v.spDuration.setSelection(DurationView.COUNT.pos)
            v.etCount.setText(it.count.toString())
        } else if (it.until != null) {
            v.spDuration.setSelection(DurationView.UNTIL.pos)
        } else {
            v.spDuration.setSelection(DurationView.INFINITELY.pos)
        }
    }

    override fun setViewNotRule() {
        selectedFreq(FreqView.NEVER.pos)
    }

    override fun setUntil(until: ZonedDateTime) {
        v.tvDate.text = until.format(formatter)
    }

    override fun onSave(it: RecurrenceRule) {
        it.setFreq(FreqView.fromPos(v.spFreq.selectedItemPosition)!!.toFreq()!!, true)

        var interval = v.etEach.text.toString().toInt()
        if (interval <= 0) {
            interval = 1
            showToast("Интервал должен быть больше 0")
        }
        it.interval = interval

        val durationPos = v.spDuration.selectedItemPosition
        if (durationPos == DurationView.COUNT.pos) {
            var count = v.etCount.text.toString().toInt()
            if (count <= 0) {
                count = 1
                showToast("Заданное колличество раз должен быть больше 0")
            }
            it.count = count

        } else if (durationPos == DurationView.UNTIL.pos) {
            it.until = toDateTimeUTC(freqPresenter.untilUTC)
        }
    }

    override fun showToast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onExit(r: String) {
        recurrenceViewModel.recurrence.postValue(r)
        InjectApplication.inject.router.exit()
    }

}