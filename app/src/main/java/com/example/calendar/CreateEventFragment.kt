package com.example.calendar;

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.content.ContextCompat
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.data.Event
import com.example.calendar.helpers.START_EVENT_KEY
import com.example.calendar.helpers.END_EVENT_KEY
import com.example.calendar.view.CreateEventInfo
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_create_event.view.*


class CreateEventFragment : MvpAppCompatFragment(), CreateEventInfo {

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
            resources.getString(R.string.default_event_text),
            arguments!!.getLong(START_EVENT_KEY),
            arguments!!.getLong(END_EVENT_KEY)
        )
    }

    private val fmt = SimpleDateFormat(
        "EE, dd/MM/yyyy HH:mm", Locale.getDefault()
    )

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

        return v
    }

    private fun initToolBar() {
        v.tbNoteCreate.navigationIcon?.setTint(
            ContextCompat.getColor(context!!, R.color.colorWhite)
        )
        v.tbNoteCreate.setNavigationOnClickListener() { createEventPresenter.onBackPressed() }
//        v.tbNoteCreate.   inflateMenu(R.menu.menu_order_info)
//        tbNoteCreate.overflowIcon?.setTint(
//            ContextCompat.getColor(context!!, R.color.colorWhite)
//        )
//        v.tbNoteCreate.getMenu().findItem(R.id.actionCancel).setOnMenuItemClickListener({ item ->
//            customerOrderInfoPresenter.cancelRequest()
//            true
//        })
//        toolbar.getMenu().findItem(R.id.actionRepeat).setOnMenuItemClickListener({ item ->
//            customerOrderInfoPresenter.repeatOrder()
//            true
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        setHasOptionsMenu(true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun updateEventInfo(e: Event) {
        v.etTextEvent.setText(e.text)
        v.etBeginDate.setText(fmt.format(e.beginDate))
        v.etEndDate.setText(fmt.format(e.endDate))
    }

    override fun showDatePickerDialog(c: Calendar, l: DatePickerDialog.OnDateSetListener) {
        val dpd = DatePickerDialog(
            context, l,
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

}