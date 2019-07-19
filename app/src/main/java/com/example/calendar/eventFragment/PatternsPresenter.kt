package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.repository.server.model.PatternRequest
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


@InjectViewState
class PatternsPresenter(
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    var patterns: ArrayList<PatternRequest>
) : MvpPresenter<PatternsSaveView>() {

    constructor(start: ZonedDateTime, end: ZonedDateTime)
            : this(start, end, arrayListOf(getPatternStub(start, end)))

    val patternStub : PatternRequest
        get() = getPatternStub(start, end)

    fun onSaveInstanceState(newPatterns: ArrayList<PatternRequest>) {
        patterns = newPatterns
    }

    fun onCloseRecurrenceSelect(rrule: String?, pos: Int) {
        patterns[pos].setRecurrence(rrule)
        viewState.updatePattern(patterns[pos], pos)
    }

    fun onCloseTimezoneSelect(t: ZoneId, pos: Int) {
        patterns[pos].timezone = t
        viewState.updatePattern(patterns[pos], pos)
    }

    companion object {
        private fun getPatternStub(start: ZonedDateTime, end: ZonedDateTime) =
            PatternRequest(
                started_at = start,
                ended_at = end,
                rrule = null,
                timezone = ZoneId.systemDefault(),
                exrules = emptyList()
            )
    }
}