package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.repository.server.model.PatternRequest
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


@InjectViewState
class PatternListSavePresenter(
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    var patterns: ArrayList<PatternRequest>
) : MvpPresenter<PatternListSaveView>() {

    constructor(start: ZonedDateTime, end: ZonedDateTime)
            : this(start, end, arrayListOf(getPatternStub(start, end)))


    init {
        viewState.setPatterns(patterns)
    }

    fun onAddPatterns() {
        viewState.addPattern(getPatternStub(start, end))
    }

    fun onSaveInstanceState(newPatterns: ArrayList<PatternRequest>) {
        patterns = newPatterns
    }

    fun onCreateView() {
        viewState.setPatterns(patterns)
    }

    fun onRecurrenceExit(rrule: String, pos: Int) {
        patterns[pos].setRecurrence(rrule)
        viewState.updatePattern(patterns[pos], pos)
    }

    companion object {
        fun getPatternStub(start: ZonedDateTime, end: ZonedDateTime) =
            PatternRequest(
                started_at = start,
                ended_at = end,
                rrule = "",
                timezone = ZoneId.systemDefault(),
                exrules = emptyList()
            )
    }
}