package com.example.calendar;

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import java.util.*
import androidx.core.content.ContextCompat
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.helpers.START_EVENT_KEY
import com.example.calendar.helpers.END_EVENT_KEY
import com.example.calendar.view.CreateEventInfoView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.presenter.BackPressedPresenter
import com.example.calendar.presenter.CreateEventPresenter
import com.example.calendar.presenter.DateClickPresenter
import com.example.calendar.view.BackPressedView
import com.example.calendar.view.DateClickView
import kotlinx.android.synthetic.main.fragment_create_event.view.*


class CreateEventFragment : MvpAppCompatFragment(),
    CreateEventInfoView, BackPressedView,
    DateClickView {

    companion object {
        fun newInstance(startEvent: Calendar, endEvent: Calendar): CreateEventFragment {
            val args = Bundle()
            args.run {
                this.putLong(START_EVENT_KEY, startEvent.timeInMillis)
                this.putLong(END_EVENT_KEY, endEvent.timeInMillis)
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
            EventRoomDatabase.getInstance(context!!).eventDao()
        )
    }

    @InjectPresenter
    lateinit var backPressedPresenter: BackPressedPresenter

    @InjectPresenter
    lateinit var dateClickPresenter: DateClickPresenter

    @ProvidePresenter
    fun provideDateClickPresenter(): DateClickPresenter {
        return DateClickPresenter(
            arguments!!.getLong(START_EVENT_KEY),
            arguments!!.getLong(END_EVENT_KEY)
        )
    }


    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

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
        v.vBegin.onClickDayListener = View.OnClickListener { dateClickPresenter.onClickBeginDay() }
        v.vBegin.onClickHourListener = View.OnClickListener { dateClickPresenter.onClickBeginHour() }
        v.vEnd.onClickDayListener = View.OnClickListener { dateClickPresenter.onClickEndDay() }
        v.vEnd.onClickHourListener = View.OnClickListener { dateClickPresenter.onClickEndHour() }

        return v
    }

    private fun initToolBar() {
        v.tbNoteCreate.setNavigationOnClickListener() { backPressedPresenter.onBackPressed() }
        v.tbNoteCreate.inflateMenu(R.menu.menu_enent_create)
        v.tbNoteCreate.menu.findItem(R.id.actionCreate).setOnMenuItemClickListener() {
            createEventPresenter.onSaveEvent(
                view!!.etTextEvent.text.toString(),
                dateClickPresenter.startEvent,
                dateClickPresenter.endEvent,
                backPressedPresenter)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        setHasOptionsMenu(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun updateDateInfo(begin: Calendar, end: Calendar) {
        v.vBegin.date = begin
        v.vEnd.date = end
    }

    override fun showDatePickerDialog(c: Calendar, l: DatePickerDialog.OnDateSetListener) {
        val dpd = DatePickerDialog(
            context!!, l,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    override fun showTimePickerDialog(c: Calendar, l: TimePickerDialog.OnTimeSetListener) {
        val tpd = TimePickerDialog(
            context, l,
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            true
        )
        tpd.show()
    }

    override fun finishView() {
        activity!!.supportFragmentManager.popBackStack()
    }

}