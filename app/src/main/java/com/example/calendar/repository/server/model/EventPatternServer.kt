package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.max
import com.example.calendar.helpers.toDateTimeUTC
import com.example.calendar.repository.db.convert.DurationConverter
import com.example.calendar.repository.db.convert.ZoneDateTimeConverter
import com.example.calendar.repository.db.convert.ZoneIdConverter
import com.example.calendar.repository.server.convert.ZonedDateTimeJsonConvert
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.*
import java.util.*

data class EventPatternServer(
    val id: Long,

    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime,

    var started_at: ZonedDateTime,
    var duration: Duration,
    var ended_at: ZonedDateTime,
    var exrules: List<EventPatternExruleServer>,
    var rrule: String,
    var timezone: ZoneId
) : Parcelable {

    val started_at_zoneid: ZonedDateTime
        get() = started_at.withZoneSameInstant(timezone)

    val started_at_local: ZonedDateTime
        get() = started_at.withZoneSameInstant(ZoneId.systemDefault())

    val ended_at_zoneid: ZonedDateTime
        get() = ended_at.withZoneSameInstant(timezone)

    val ended_at_local: ZonedDateTime
        get() = ended_at.withZoneSameInstant(ZoneId.systemDefault())

    fun isRecurrence() = rrule.isNotEmpty()

    fun getRecurrenceRuleWithZoneId(): RecurrenceRule? {
        if (!isRecurrence()) {
            return null
        }
        val r = RecurrenceRule(rrule)
        if (r.until != null) {
            r.until =
                DateTime(DateTime.GREGORIAN_CALENDAR_SCALE, DateTimeUtils.toTimeZone(timezone), r.until)
        }
        return r
    }


    fun removeRecurrence() {
        rrule = ""
        ended_at = calculateEndedAt()
    }

    fun setRecurrence(r: String) {
        if (r.isEmpty()) {
            removeRecurrence()
            return
        }
        val recurrence = RecurrenceRule(r)
        // until to UTF
        if (recurrence.until != null) {
            recurrence.until = DateTime(
                DateTime.GREGORIAN_CALENDAR_SCALE, TimeZone.getTimeZone("UTF"), recurrence.until
            )
        }
        rrule = recurrence.toString()
        ended_at = calculateEndedAt()
    }

    fun setUntil(until: ZonedDateTime) {
        if (isRecurrence()) {
            val recurrence = RecurrenceRule(rrule)
            recurrence.until = toDateTimeUTC(until.withZoneSameInstant(ZoneOffset.UTC))
            rrule = recurrence.toString()
            ended_at = calculateEndedAt()
        }
    }

    private fun calculateEndedAt(): ZonedDateTime {
        val maxDate = fromLongUTC(Long.MAX_VALUE)
        var newEndedAt = started_at.plus(duration)

        if (!isRecurrence()) {
            return newEndedAt
        }

        val recurrence = RecurrenceRule(rrule)
        if (recurrence.isInfinite) {
            newEndedAt = maxDate
        }
        else if (recurrence.until != null) {
            // the RRULE includes an UNTIL
            newEndedAt = max(fromDateTimeUTC(recurrence.until), newEndedAt)
        }
        else if (recurrence.count != null) {
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

    constructor(parcel: Parcel) :
            this(
                id = parcel.readLong(),
                // todo a lof of create
                created_at = ZoneDateTimeConverter().toZoneDateTime(parcel.readLong())!!,
                updated_at = ZoneDateTimeConverter().toZoneDateTime(parcel.readLong())!!,
                started_at = ZoneDateTimeConverter().toZoneDateTime(parcel.readLong())!!,
                duration = DurationConverter().toDuration(parcel.readLong())!!,
                ended_at = ZoneDateTimeConverter().toZoneDateTime(parcel.readLong())!!,
                exrules = emptyList<EventPatternExruleServer>(),
                rrule = parcel.readString()!!,
                timezone = ZoneIdConverter().toZoneId(parcel.readString())!!
            )
    {
        parcel.readTypedList(exrules, EventPatternExruleServer.CREATOR)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeLong(id)
        dest.writeLong(ZoneDateTimeConverter().fromZoneDateTime(created_at)!!)
        dest.writeLong(ZoneDateTimeConverter().fromZoneDateTime(updated_at)!!)
        dest.writeLong(ZoneDateTimeConverter().fromZoneDateTime(started_at)!!)
        dest.writeLong(DurationConverter().fromDuration(duration)!!)
        dest.writeLong(ZoneDateTimeConverter().fromZoneDateTime(ended_at)!!)
        dest.writeString(rrule)
        dest.writeString(ZoneIdConverter().fromZoneId(timezone))

        dest.writeTypedList(exrules)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventPatternServer> {
        override fun createFromParcel(parcel: Parcel): EventPatternServer{
            return EventPatternServer(parcel)
        }

        override fun newArray(size: Int): Array<EventPatternServer?> {
            return arrayOfNulls(size)
        }
    }
}
