package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import ru.terrakok.cicerone.Router
import java.lang.IllegalArgumentException

@InjectViewState
class EditEventPresenter(
    private val router: Router,
    private val eventRepository: EventRepository,
    var eventInstance: EventInstance?
) : BaseMvpSubscribe<EditEventView>() {

    private var isDelete = false

    val isEditMode = eventInstance != null

    val patterns = ArrayList<EventPatternServer>()
    val delete_patterns = ArrayList<EventPatternServer>()

    init {
        if (isEditMode) {
            val e = eventInstance!!
            val u = eventRepository.getPatterns(e.entity.id)
                .subscribe({
                    patterns.addAll(it)
                    viewState.updateEventInfo(
                        e.user,
                        e.entity.eventRequest,
                        it.map { p -> p.patternRequest } as ArrayList<PatternRequest>)
                }, {
                    onLoadingFailed(it.toString())
                })
            unsubscribeOnDestroy(u)
        }
    }

    fun addPattern(p: PatternRequest) {
        if (!isEditMode) {
            return
        }
        patterns.add(EventPatternServer.getStubPatternServer(p))
    }

    fun deletePattern(pos: Int) {
        if (!isEditMode) {
            return
        }
        delete_patterns.add(patterns[pos])
        patterns.removeAt(pos)
    }

    fun onUpdateAll(eventRequest: EventRequest, newPatterns: List<PatternRequest>) {
        if (!isEditMode) {
            return
        }
        val entity = eventInstance!!.entity

        if (newPatterns.size != patterns.size) {
            throw IllegalArgumentException()
        }

        val updates = patterns.zip(newPatterns).map {
            val p = it.first
            p.patternRequest = it.second
            p
        }

        val create_pattern = updates.filter { it.id < 0 }.map { it.patternRequest }
        val update_pattern = updates.filter { it.id >= 0 }
        val pattern_delete = delete_patterns.filter { it.id >= 0 }.map { it.id }

        val com = listOf(
            eventRepository.updateEvent(entity),
            eventRepository.updatePatterns(update_pattern),
            eventRepository.createPatterns(entity.id, create_pattern),
            eventRepository.deletePatterns(pattern_delete)
        )
        val u = Completable.merge(com)
            .subscribe(
                {
                    // todo forbined
                    // todo auth
                    // todo notfind
                    viewState.showError("Update!!")
                    router.exit()
                },
                { error ->
                    onLoadingFailed(error.toString())
                    router.exit()
                })
        unsubscribeOnDestroy(u)
    }

    fun onDeleteAll() {
        if (isDelete || !isEditMode) {
            return
        }

        isDelete = true
        val sub = eventRepository.deleteEvent(eventInstance!!.entity.id)
            .subscribe(
                {
                    onDeleteLoading()
                },
                { error ->
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    private fun onLoadingFailed(error: String) {
        viewState.showError(error);
    }

    private fun onDeleteLoading() {
        router.exit()
    }
}