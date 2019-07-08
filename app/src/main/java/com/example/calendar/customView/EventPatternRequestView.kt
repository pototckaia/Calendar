package com.example.calendar.customView

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.eventFragment.DateClickPresenter
import com.example.calendar.eventFragment.DateClickView
import com.example.calendar.helpers.END_EVENT_KEY
import com.example.calendar.helpers.START_EVENT_KEY
import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.fromStringToZoned
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import kotlinx.android.synthetic.main.view_event_pattern_request.view.*
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

class EventPatternRequestView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    DateClickView {

    var v: View = LayoutInflater.from(context).inflate(
        R.layout.view_event_pattern_request, this, true
    )

    @InjectPresenter
    lateinit var dateClickPresenter: DateClickPresenter

    @ProvidePresenter
    fun provideDateClickPresenter(): DateClickPresenter {
        return DateClickPresenter(
            { d: ZonedDateTime -> validateStartEvent(d) },
            { true }
        )
    }

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EventPatternRequestView)
        attr.recycle()

        v.vBegin.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickBeginDay() }
        v.vBegin.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickBeginHour() }
        v.vEnd.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickEndDay() }
        v.vEnd.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickEndHour() }
    }

    override fun updateDateInfo(startLocal: ZonedDateTime, endLocal: ZonedDateTime) {
        v.vBegin.setDate(startLocal)
        v.vEnd.setDate(endLocal)
    }

    override fun showDatePickerDialog(local: ZonedDateTime, l: DatePickerDialog.OnDateSetListener) {
        val dpd = MaterialDatePickerDialog(local, l, context,
            DialogInterface.OnCancelListener { var1: DialogInterface -> })
        dpd.show()
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

    private fun validateStartEvent(start: ZonedDateTime): Boolean {
//        val rule = recurrenceEventPresenter.getRule()
//        if (rule.isNotEmpty() && RecurrenceRule(rule).until != null) {
//            val until = fromDateTimeUTC(RecurrenceRule(rule).until)
//            val startUTC = start.withZoneSameInstant(ZoneOffset.UTC)
//            if (startUTC >= until) {
//                Toast
//                    .makeText(
//                        context,
//                        "Дата начала события не может быть позже даты ДО в правиле переодичности",
//                        Toast.LENGTH_SHORT
//                    )
//                    .show()
//            }
//            return startUTC < until
//        }
        return true
    }


    private var mParentDelegate: MvpDelegate<*>? = null
    private var mMvpDelegate: MvpDelegate<DateClickView>? = null

    val mvpDelegate: MvpDelegate<DateClickView>
        get() {
            if (mMvpDelegate != null) {
                return mMvpDelegate!!
            }
            mMvpDelegate = MvpDelegate(this)
            mMvpDelegate!!.setParentDelegate(mParentDelegate!!, id.toString())
            return mMvpDelegate!!
        }

    fun init(parentDelegate: MvpDelegate<*>) {
        mParentDelegate = parentDelegate
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mvpDelegate.onSaveInstanceState()
        mvpDelegate.onDetach()
    }
}