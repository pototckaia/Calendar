package com.example.calendar.eventFragment

import org.dmfs.rfc5545.Weekday
import org.dmfs.rfc5545.recur.Freq
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.DayOfWeek


enum class eFreqView(val pos: Int) {
    NEVER(0), DAILY(1), WEEKLY(2), MONTHLY(3), YEARLY(4);

    fun toFreq(): Freq? =
        when (this) {
            DAILY -> Freq.DAILY
            WEEKLY -> Freq.WEEKLY
            MONTHLY -> Freq.MONTHLY
            YEARLY -> Freq.YEARLY
            NEVER -> null
        }

    companion object {
        fun fromPos(pos: Int): eFreqView? {
            for (f in eFreqView.values()) {
                if (f.pos == pos) {
                    return f
                }
            }
            return null
        }

        fun fromFreq(f: Freq): eFreqView? =
            when (f) {
                Freq.DAILY -> DAILY
                Freq.WEEKLY -> WEEKLY
                Freq.MONTHLY -> MONTHLY
                Freq.YEARLY -> YEARLY
                Freq.SECONDLY, Freq.MINUTELY, Freq.HOURLY -> null
            }


    }
}

enum class eDurationView(val pos: Int) {
    INFINITELY(0), COUNT(1), UNTIL(2);

    companion object {
        fun fromPos(pos: Int): eDurationView? {
            for (f in eDurationView.values()) {
                if (f.pos == pos) {
                    return f
                }
            }
            return null
        }

    }
}

enum class eWeekView(val pos: Int, val dayOfWeek: DayOfWeek) {
    MO(0, DayOfWeek.MONDAY),
    TU(1, DayOfWeek.TUESDAY),
    WE(2, DayOfWeek.WEDNESDAY),
    TH(3, DayOfWeek.THURSDAY),
    FR(4, DayOfWeek.FRIDAY),
    SA(5, DayOfWeek.SATURDAY),
    SU(6, DayOfWeek.SUNDAY);

    fun toWeekNum(): RecurrenceRule.WeekdayNum =
        when (this) {
            MO -> RecurrenceRule.WeekdayNum(0, Weekday.MO)
            TU -> RecurrenceRule.WeekdayNum(0, Weekday.TU)
            WE -> RecurrenceRule.WeekdayNum(0, Weekday.WE)
            TH -> RecurrenceRule.WeekdayNum(0, Weekday.TH)
            FR -> RecurrenceRule.WeekdayNum(0, Weekday.FR)
            SA -> RecurrenceRule.WeekdayNum(0, Weekday.SA)
            SU -> RecurrenceRule.WeekdayNum(0, Weekday.SU)
        }

    companion object {
        fun fromPos(pos: Int): eWeekView? {
            for (f in eWeekView.values()) {
                if (f.pos == pos) {
                    return f
                }
            }
            return null
        }

        fun fromDayOfWeek(d: DayOfWeek): eWeekView? {
            for (f in eWeekView.values()) {
                if (f.dayOfWeek == d) {
                    return f
                }
            }
            return null
        }

        fun fromWeekNum(d: RecurrenceRule.WeekdayNum): eWeekView? {
            for (f in eWeekView.values()) {
                if (f.toWeekNum().weekday == d.weekday) {
                    return f
                }
            }
            return null
        }
    }

}
