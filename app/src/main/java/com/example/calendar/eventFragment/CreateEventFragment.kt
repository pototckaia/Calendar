package com.example.calendar.eventFragment;

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import java.util.*
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.helpers.START_EVENT_KEY
import com.example.calendar.helpers.END_EVENT_KEY
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.customView.MaterialDatePickerDialog
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.remove.BackPressedPresenter
import com.example.calendar.remove.BackPressedView
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
            // todo inject
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_create_event,
            container, false)

        initToolBar()

        v.vBegin.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickBeginDay() }
        v.vBegin.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickBeginHour() }
        v.vEnd.onDayClickListener = View.OnClickListener { dateClickPresenter.onClickEndDay() }
        v.vEnd.onHourClickListener = View.OnClickListener { dateClickPresenter.onClickEndHour() }

        return v
    }

    private fun initToolBar() {
        v.tbNoteCreate.setNavigationOnClickListener { backPressedPresenter.onBackPressed() }
        v.tbNoteCreate.inflateMenu(R.menu.menu_enent_create)
        v.tbNoteCreate.menu.findItem(R.id.actionCreate).setOnMenuItemClickListener {
            createEventPresenter.onSaveEvent(
                view!!.etTextEvent.text.toString(),
                dateClickPresenter.startEvent,
                dateClickPresenter.endEvent,
                backPressedPresenter)
            true
        }
    }

    override fun updateDateInfo(begin: Calendar, end: Calendar) {
        v.vBegin.setDate(begin)
        v.vEnd.setDate(end)
    }

    override fun showDatePickerDialog(c: Calendar, l: DatePickerDialog.OnDateSetListener) {
        val dpd = MaterialDatePickerDialog.newInstance(c, l)
        dpd.show(activity?.supportFragmentManager, "date-picker")
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