package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.dmfs.rfc5545.recur.Freq
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.ZonedDateTime


enum class FreqView(val pos: Int) {
    NEVER(0), DAILY(1), WEEKLY(2), MONTHLY(3), YEARLY(4);

    fun toFreq() : Freq? =
        when (this) {
            DAILY -> Freq.DAILY
            WEEKLY -> Freq.WEEKLY
            MONTHLY -> Freq.MONTHLY
            YEARLY -> Freq.YEARLY
            NEVER -> null
        }

    companion object {
        fun fromPos(pos: Int) : FreqView? {
            for (f in FreqView.values()) {
                if (f.pos == pos) {
                    return f
                }
            }
            return null
        }

        fun fromFreq(f: Freq) : FreqView? =
            when (f) {
                Freq.DAILY -> DAILY
                Freq.WEEKLY -> WEEKLY
                Freq.MONTHLY -> MONTHLY
                Freq.YEARLY -> YEARLY
                Freq.SECONDLY, Freq.MINUTELY, Freq.HOURLY -> null
            }


    }
}

enum class DurationView(val pos: Int) {
    INFINITELY(0), COUNT(1), UNTIL(2);

    companion object {
        fun fromPos(pos: Int) : DurationView? {
            for (f in DurationView.values()) {
                if (f.pos == pos) {
                    return f
                }
            }
            return null
        }

    }
}


@StateStrategyType(AddToEndSingleStrategy::class)
interface RecurrenceRuleView : MvpView {

    fun setViewRule(it: RecurrenceRule)

    fun setViewNotRule()

    fun setUntil(until: ZonedDateTime)

    @StateStrategyType(SkipStrategy::class)
    fun openCalendar(until: ZonedDateTime, l : DatePickerDialog.OnDateSetListener)

    @StateStrategyType(SkipStrategy::class)
    fun showToast(s: String)

    @StateStrategyType(SkipStrategy::class)
    fun onSave(it: RecurrenceRule)

    @StateStrategyType(SkipStrategy::class)
    fun onExit(r: String)
}