package com.example.calendar.helpers

import com.example.calendar.helpers.convert.*
import com.example.calendar.repository.server.model.EventInstance
import com.example.calendar.repository.server.model.EventPatternServer
import com.example.calendar.repository.server.model.EventServer
import org.dmfs.rfc5545.Weekday
import org.dmfs.rfc5545.recur.Freq
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

fun isRecurrence(rrule: String) = rrule.isNotEmpty()

fun isFromPeriod(start_event: ZonedDateTime, end_event: ZonedDateTime,
                         start: ZonedDateTime, end: ZonedDateTime) : Boolean {
    return (start_event <= end && end_event >= start)
}

// [start, end]
fun getEventInstances(
    event: EventServer,
    pattern: EventPatternServer,
    start: ZonedDateTime,
    end: ZonedDateTime
): List<EventInstance> {

    val maxInstances = 100000
    val instances = arrayListOf<EventInstance>()

    val startLocal = start.withZoneSameInstant(ZoneId.systemDefault())
    val endLocal = end.withZoneSameInstant(ZoneId.systemDefault())

    val startRange = toDateTime(startLocal, toTimeZone(pattern.timezone))
    val endRange = toDateTime(endLocal, toTimeZone(pattern.timezone))

    if (!isRecurrence(pattern.rrule)) {
        val eventInstance = EventInstance(event, pattern, pattern.started_at, pattern.started_at.plus(pattern.duration))
        if (isFromPeriod(
                eventInstance.started_at_local, eventInstance.ended_at_local,
                startLocal, endLocal)) {
            instances.add(eventInstance)
        }
        return instances
    }

    val patternRequest = pattern.patternRequest
    val recurrence = patternRequest.getRecurrenceRuleWithZoneId()!!
    val startRecurrence = toDateTime(pattern.started_at, toTimeZone(pattern.timezone))

    val it = recurrence.iterator(startRecurrence)
    var counter = 0
    while (it.hasNext() && (!recurrence.isInfinite || counter < maxInstances)) {
        val startInstance = it.nextDateTime()

        if (recurrence.until != null &&
            (startInstance.after(recurrence.until) || startInstance == recurrence.until)
        ) {
            break
        }

        if (startInstance.after(endRange) || startInstance == endRange) {
            break;
        }
        val startNewEvent = fromDateTime(startInstance)
        val eventInstance = EventInstance(event, pattern, startNewEvent, startNewEvent.plus(pattern.duration))

        if (isFromPeriod(
                eventInstance.started_at_local, eventInstance.ended_at_local,
                startLocal, endLocal)) {
            instances.add(eventInstance)
            counter++
        }
    }
    return instances
}


fun calculateEndedAt(
    started_at: ZonedDateTime,
    duration: Duration,
    rrule: String
): ZonedDateTime {
    val maxDate = zonedDateTime_cn.toZonedDateTime(Long.MAX_VALUE)
    var newEndedAt = started_at.plus(duration)

    if (!isRecurrence(rrule)) {
        return newEndedAt
    }

    val recurrence = RecurrenceRule(rrule)
    if (recurrence.isInfinite) {
        newEndedAt = maxDate
    } else if (recurrence.until != null) {
        // the RRULE includes an UNTIL
        newEndedAt = max(fromDateTimeUTC(recurrence.until), newEndedAt)
    } else if (recurrence.count != null) {
        // The RRULE has a limit, so calculate
        var startedAtDateTime = toDateTimeUTC(started_at)
        val it = recurrence.iterator(startedAtDateTime)
        if (it.hasNext()) {
            it.skipAllButLast()
            startedAtDateTime = it.nextDateTime()
        }
        val startedAtZonedDate = fromDateTimeUTC(startedAtDateTime)
        newEndedAt = max(startedAtZonedDate.plus(duration), newEndedAt)
    }

    return newEndedAt
}

private fun getPosName(p: Int?) = when (p) {
        0, null -> { "" }
        else -> {
            var first = p.toString()
            if (p < 0) {
                first += " с конца"
            }
            first
        }
    }

// list of days of the week - all
// pos - 1..53, -53..-1
private fun getBYDAY(w: RecurrenceRule.WeekdayNum) : String {
    var week = ""
    when(w.weekday) {
        Weekday.SU -> week = "вс"
        Weekday.MO -> week = "пн"
        Weekday.TU -> week = "вт"
        Weekday.FR -> week = "пт"
        Weekday.WE -> week = "ср"
        Weekday.TH -> week = "чт"
        Weekday.SA -> week = "сб"
        null -> {}
    }
    val first = getPosName(w.pos)
    if (first.isEmpty())
        return week
    return "$first $week"
}

// list of months of the year - all
// 1..12
private fun getBYMONTH(p: Int?) = when (p) {
    1 -> { "янв." }
    2 -> { "фев."}
    3 -> { "мар." }
    4 -> { "апр." }
    5 -> { "май" }
    6 -> { "июн." }
    7 -> { "июл." }
    8 -> { "авг." }
    9 -> { "сент." }
    10 -> { "окт." }
    11 -> { "нояб." }
    12 -> { "дек." }
    else -> { "" }
}

//BYMONTHDAY
// list of days of the month - Not valid FREQ = WEEKLY
// 1..31, -31..-1

//BYSETPOS
// A list of set positions to consider when iterating the instances - all

//BYYEARDDAY
// list of days of the year - Not valid FREQ = DAILY, WEEKLY, MONTHLT
// 1..366, -366..-1

//BYWEEKNO
// list of ordinals specifying week of the year in which weeks the instances recur -
// Not valid FREQ = DAILY, WEEKLY, MONTHLT
// // pos - 1..53, -53..-1
// wkst - week start day

private fun getListPos(
    r: RecurrenceRule,
    p: RecurrenceRule.Part,
    prefix: String = "", postfix: String = "",
    getPos: (Int?) -> String = ::getPosName) : String {
    val part = r.getByPart(p)
    part?.run {
        val v = part.map { getPos(it) }.joinToString(separator = ", ")
        return "$prefix$v$postfix"
    }
    return ""
}
// Freq.HOURLY, Freq.MINUTELY, FREQ.SECONDLY
fun getRecurrenceName(r: String) : String {
    if (!isRecurrence(r)) {
        return r
    }

    val fmt_day = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val rrule = RecurrenceRule(r)

    var freq = ""
    when (rrule.freq) {
        Freq.DAILY -> { freq = "Ежедневно" }
        Freq.WEEKLY -> { freq = "Еженедельно" }
        Freq.MONTHLY ->  { freq = "Ежемесячно" }
        Freq.YEARLY -> { freq = "Ежегодно" }
        else -> {}
    }

    var interval = ""
    if (rrule.interval > 1) {
        interval = "\nс интервалом ${rrule.interval}"
    }

    val bymonth = getListPos(rrule, RecurrenceRule.Part.BYMONTH, "в ", "", ::getBYMONTH)
    var byweekday = ""
    if (rrule.byDayPart != null) {
        byweekday = "в " + rrule.byDayPart.map { getBYDAY(it) }.joinToString(separator = ", ")
    }
    val bymonthday = getListPos(rrule, RecurrenceRule.Part.BYMONTHDAY, "", " числа")
    val bysetpos = getListPos(rrule, RecurrenceRule.Part.BYSETPOS, "каждый: ")

    val byyearday = getListPos(rrule, RecurrenceRule.Part.BYYEARDAY, "на ", " день")
    val byweekno = getListPos(rrule, RecurrenceRule.Part.BYWEEKNO, "в недели ")

    var add = "$bymonth $byweekday $bymonthday"
    if (rrule.freq == Freq.YEARLY) {
        add += "$byyearday $byweekno"
    }
    add += bysetpos

    var end = ""
    if (rrule.count != null) {
        end = "кол-во ${rrule.count}"
    } else if (rrule.until != null) {
        val until = fromDateTimeUTC(rrule.until).withZoneSameInstant(ZoneId.systemDefault())
        end = "до ${until.format(fmt_day)}"
    } else {
        end = "бесконечно"
    }

    return "$freq $add $interval \n$end"
}