package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EventRequest
import com.example.calendar.repository.server.model.PatternRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router


@InjectViewState
class CreateEventPresenter(
    private val router: Router,
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<CreateEventView>() {

    fun onSaveEvent(
        event: EventRequest,
        pattern: ArrayList<PatternRequest>) {

        for (i in 0 until pattern.size) {
            if (!check(i, pattern[i])) {
                return
            }
        }

        val subscription = eventRepository.insertEvent(event, pattern)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                router.exit()
            }, {
                viewState.showError(it.toString())
            })

        unsubscribeOnDestroy(subscription)
    }

    private fun check(pos: Int, p: PatternRequest) : Boolean {
        if (p.started_at >= p.ended_at) {
            viewState.showError("Ошибка: начало позже крайней границы")
            return false
        }
        if (p.started_at >= p.started_at.plus(p.duration)) {
            viewState.showError("Ошибка: продолжительность события отрицательна")
            return false
        }
        return true
    }
}