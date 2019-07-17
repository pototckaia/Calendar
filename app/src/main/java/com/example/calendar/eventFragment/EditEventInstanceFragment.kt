package com.example.calendar.eventFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.helpers.EVENT_INSTANCE_KEY
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import com.example.calendar.repository.server.model.*
import kotlinx.android.synthetic.main.fragment_event_instant.view.*
import org.threeten.bp.ZoneId


//enum class ModifyView {
//    Update, Delete
//}
//
//enum class RecurrenceModifyViw(val pos: Int) {
//    Future(0), All(1)
//}

class EditEventInstanceFragment : MvpAppCompatFragment(),
    EditEventInstanceView {

    companion object {
        fun newInstance(event: EventInstance): EditEventInstanceFragment {
            val args = Bundle()
            args.run {
                this.putParcelable(EVENT_INSTANCE_KEY, event)
            }
            val f = EditEventInstanceFragment()
            f.arguments = args
            return f
        }
    }

    @InjectPresenter
    lateinit var editEventPresenter: EditEventInstancePresenter

    @ProvidePresenter
    fun provideEditEventPresenter(): EditEventInstancePresenter {
        return EditEventInstancePresenter(
            router,
            InjectApplication.inject.repository,
            arguments!!.getParcelable<EventInstance>(EVENT_INSTANCE_KEY)!!
        )
    }

    private val router = InjectApplication.inject.router

    private lateinit var v: View
    lateinit var exitViewModel: ExitEventPatternViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_event_instant,
            container, false
        )

        initToolBar()

        v.vEventPatternRequest.setTrashInvisible()

        v.vEventPatternRequest.viewModel.liveData.observe(this, Observer {
            editEventPresenter.onPatternChange(it!!)
        })

        exitViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExitEventPatternViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")

        exitViewModel.recurrence.observe(this, Observer { r ->
            onCloseRecurrenceSelect(r)
        })

        exitViewModel.timezone.observe(this, Observer { p ->
            onClostTimezoneSelect(p)
        })

        v.vEventPatternRequest.setTimeZoneOnClick(this::onTimeZoneClick)
        v.vEventPatternRequest.setRecurrenceOnClick(this::onRecurrenceRuleClick)
        v.etTasks.setOnClickListener { onTaskClick() }

        return v
    }

    private fun initToolBar() {
        v.tbEventInstance.setNavigationOnClickListener { router.exit() }
        v.tbEventInstance.inflateMenu(R.menu.menu_event_edit)
        v.tbEventInstance.setOnMenuItemClickListener {
            onItemSelected(it);
            true
        }

        v.bnvEventInstance.itemIconTintList = null
        v.bnvEventInstance.menu.getItem(0).setCheckable(false)
        v.bnvEventInstance.setOnNavigationItemSelectedListener {
            onItemSelected(it)
            false
        }
    }

    override fun updateEventInfo(ownerName: String, e: EventRequest, p: PatternRequest) {
        v.etOwner.setText(ownerName)
        v.vEventRequest.eventRequest = e
        v.vEventPatternRequest.viewModel.liveData.postValue(p)
    }

    private fun onCloseRecurrenceSelect(p: String) {
        if (exitViewModel.isActivate()) {
            v.vEventPatternRequest.viewModel.setRecurrence(p)
            exitViewModel.deactivate()
        }
    }

    private fun onClostTimezoneSelect(p: ZoneId) {
        if (exitViewModel.isActivate()) {
            v.vEventPatternRequest.viewModel.setTimeZone(p)
            exitViewModel.deactivate()
        }
    }

    private fun onRecurrenceRuleClick(v: PatternRequest) {
        exitViewModel.activate()
        router.navigateTo(Screens.FreqScreen(v.startedAtTimezone, v.rrule))
    }

    private fun onTimeZoneClick() {
        exitViewModel.activate()
        router.navigateTo(Screens.TimeZoneSelectScreen())
    }

    private fun onItemSelected(item: MenuItem?) {
        when (item?.itemId) {
            R.id.bottomActionSave -> {
                onUpdateClick()
            }
            R.id.bottomActionDelete -> {
                onDeleteClick()
            }
            R.id.bottomActionShare -> {
                onShareClick()
            }
        }
    }

    override fun showError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

    private fun onUpdateClick() {
        editEventPresenter.onUpdateAll(v.vEventRequest.eventRequest)
    }

    private fun onDeleteClick() {
        editEventPresenter.onDeleteAll()
    }

    private fun onShareClick() {
        router.navigateTo(Screens.CreateEventPermissionScreen(
            editEventPresenter.eventInstance.entity.id,
            editEventPresenter.eventInstance.pattern.id
        ))
    }

    private fun onTaskClick() {
        Toast.makeText(context, "Не работает", Toast.LENGTH_LONG).show()
    }


//    private fun showChoice(m: ModifyView) {
//        val builder = AlertDialog.Builder(context!!)
//        when (m) {
//            ModifyView.Update -> builder.setTitle(R.string.title_choice_update)
//            ModifyView.Delete -> builder.setTitle(R.string.title_choice_delete)
//        }
//
//        builder.setItems(
//            R.array.choice_variance
//        )
//        { _, pos -> onRecurrenceModifyModeView(m, pos) }
//
//        val dialog = builder.create()
//        dialog.show()
//    }
//
//    private fun onRecurrenceModifyModeView(m: ModifyView, pos: Int) =
//        when (pos) {
//            RecurrenceModifyViw.Future.pos -> {
//                when (m) {
//                    ModifyView.Update ->
//                        editEventPresenter.onUpdateFuture(
//                            v.etTextEvent.text.toString(),
//                            "TODO",
//                            dateClickPresenter.start,
//                            dateClickPresenter.end,
//                            recurrenceEventPresenter.getRule()
//                        )
//                    ModifyView.Delete ->
//                        editEventPresenter.onDeleteFuture()
//                }
//            }
//            RecurrenceModifyViw.All.pos -> {
//                when (m) {
//                    ModifyView.Update ->
//                        editEventPresenter.onUpdateAll(
//                            v.etTextEvent.text.toString(),
//                            "TODO",
//                            dateClickPresenter.start,
//                            dateClickPresenter.end,
//                            recurrenceEventPresenter.getRule()
//                        )
//                    ModifyView.Delete ->
//                        editEventPresenter.onDeleteAll()
//                }
//            }
//            else -> {
//            }
//        }
}