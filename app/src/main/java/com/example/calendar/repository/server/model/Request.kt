package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.toDateTimeUTC
import com.example.calendar.repository.*
import com.example.calendar.repository.db.convert.DurationConverter
import com.example.calendar.repository.db.convert.ZonedDateTimeConverter
import com.example.calendar.repository.db.convert.ZoneIdConverter
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.*
import java.util.*


data class RruleStructure(
    var rrule: String
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeString(rrule)
    }

    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<RruleStructure> {
        override fun createFromParcel(parcel: Parcel) = RruleStructure(parcel)
        override fun newArray(size: Int): Array<RruleStructure?> = arrayOfNulls(size)
    }
}

data class EventRequest(
    val details: String,
    val location: String,
    val name: String,
    val status: String
) {

    constructor(entity: EventServer)
            : this(
        details = entity.details,
        location = entity.location,
        name = entity.name,
        status = entity.status
    )
}

data class PatternRequest(
    var duration: Duration,
    var ended_at: ZonedDateTime,
    val exrules: List<RruleStructure>,
    var rrule: String,
    var started_at: ZonedDateTime,
    val timezone: ZoneId
) : Parcelable {

    val started_at_zoneid: ZonedDateTime
        get() = started_at.withZoneSameInstant(timezone)

    val started_at_local: ZonedDateTime
        get() = started_at.withZoneSameInstant(ZoneId.systemDefault())

    val ended_at_zoneid: ZonedDateTime
        get() = ended_at.withZoneSameInstant(timezone)

    val ended_at_local: ZonedDateTime
        get() = ended_at.withZoneSameInstant(ZoneId.systemDefault())

    constructor(
        started_at: ZonedDateTime,
        ended_at: ZonedDateTime,
        rrule: String,
        exrules: List<String>,
        timezone: ZoneId
    ) : this(
        // todo check duration
        started_at = started_at.withZoneSameInstant(ZoneOffset.UTC),
        duration = Duration.between(started_at, ended_at),
        exrules = exrules.map { RruleStructure(it) },
        timezone = timezone,
        rrule = rrule,
        ended_at = calculateEndedAt(started_at, Duration.between(started_at, ended_at), rrule)
    )

    fun getRecurrenceRuleWithZoneId(): RecurrenceRule? {
        if (!isRecurrence(rrule)) {
            return null
        }
        val r = RecurrenceRule(rrule)
        if (r.until != null) {
            r.until = DateTime(
                DateTime.GREGORIAN_CALENDAR_SCALE, DateTimeUtils.toTimeZone(timezone), r.until
            )
        }
        return r
    }

    fun removeRecurrence() {
        rrule = ""
        ended_at = calculateEndedAt(started_at, duration, rrule)
    }

    fun setStartedAt(s: ZonedDateTime) {
        started_at = s.withZoneSameInstant(ZoneOffset.UTC)
    }

    fun setDuration(e: ZonedDateTime) {
        duration = Duration.between(started_at, e.withZoneSameInstant(ZoneOffset.UTC))
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
                DateTime.GREGORIAN_CALENDAR_SCALE, TimeZone.getTimeZone("UTC"), recurrence.until
            )
        }
        rrule = recurrence.toString()
        ended_at = calculateEndedAt(started_at, duration, rrule)
    }

    fun setUntil(until: ZonedDateTime) {
        if (isRecurrence(rrule)) {
            val recurrence = RecurrenceRule(rrule)
            recurrence.until = toDateTimeUTC(until.withZoneSameInstant(ZoneOffset.UTC))

            rrule = recurrence.toString()
            ended_at = calculateEndedAt(started_at, duration, rrule)
        }
    }


    constructor(parcel: Parcel) :
            this(
                started_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                duration = duration_cn.toDuration(parcel.readLong())!!,
                ended_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                rrule = parcel.readString()!!,
                timezone = zoneId_cn.toZoneId(parcel.readString())!!,
                exrules = emptyList<RruleStructure>()
            )
    {
        parcel.readTypedList(exrules, RruleStructure.CREATOR)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(started_at)!!)
        dest.writeLong(duration_cn.fromDuration(duration)!!)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(ended_at)!!)
        dest.writeString(rrule)
        dest.writeString(zoneId_cn.fromZoneId(timezone))
        dest.writeTypedList(exrules)
    }

    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<PatternRequest> {
        override fun createFromParcel(parcel: Parcel) = PatternRequest(parcel)
        override fun newArray(size: Int): Array<PatternRequest?> = arrayOfNulls(size)
    }

}

data class TaskRequest(
    val deadline_at: ZonedDateTime,
    val details: String,
    val name: String,
    val parent_id: Long,
    val status: String
)

enum class PermissionAction {
    READ, UPDATE, DELETE;
}

enum class EntityType {
    EVENT, PATTERN, TASK;
}

data class PermissionRequest(
    val action: PermissionAction,
    val entity_id: Long,
    val entity_type: EntityType
)