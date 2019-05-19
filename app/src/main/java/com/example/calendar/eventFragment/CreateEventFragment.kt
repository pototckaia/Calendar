package com.example.calendar.eventFragment;

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.helpers.START_EVENT_KEY
import com.example.calendar.helpers.END_EVENT_KEY
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.customView.MaterialDatePickerDialog
import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.toLongUTC
import com.example.calendar.inject.InjectApplication
import kotlinx.android.synthetic.main.fragment_create_event.view.*
import org.dmfs.rfc5545.recur.Freq
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


class CreateEventFragment : MvpAppCompatFragment(),
    CreateEventInfoView, DateClickView {

    companion object {
        fun newInstance(
            startEvent: ZonedDateTime, endEvent: ZonedDateTime
        ): CreateEventFragment {
            val args = Bundle()
            args.run {
                this.putLong(START_EVENT_KEY, toLongUTC(startEvent))
                this.putLong(END_EVENT_KEY, toLongUTC(endEvent))
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
            fromLongUTC(arguments!!.getLong(START_EVENT_KEY)),
            fromLongUTC(arguments!!.getLong(END_EVENT_KEY))
        )
    }

    // todo inject
    // todo onDelete
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
        v.tbNoteCreate.setNavigationOnClickListener { router.exit() }
        v.tbNoteCreate.inflateMenu(R.menu.menu_enent_create)
        v.tbNoteCreate.menu.findItem(R.id.actionCreate).setOnMenuItemClickListener {
            onSave()
            true
        }
    }

    private fun onSave() {
        val rule_daily = RecurrenceRule(Freq.DAILY)
        rule_daily.count = 5

        createEventPresenter.onSaveEvent(
            view!!.etTextEvent.text.toString(),
            "TODO",
            dateClickPresenter.startLocal.withZoneSameInstant(ZoneOffset.UTC),
            dateClickPresenter.endLocal.withZoneSameInstant(ZoneOffset.UTC),
            rule_daily
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
}