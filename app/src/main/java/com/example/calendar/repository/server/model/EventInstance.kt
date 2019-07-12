package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.repository.zonedDateTime_cn
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


data class Event(
    val entity: EventServer,
    val pattern: List<EventPatternServer>
)

data class EventInstance(
    val entity: EventServer,
    val pattern: EventPatternServer,

    // [start, end]
    var started_at: ZonedDateTime,
    var ended_at: ZonedDateTime
) : Parcelable {

    val started_at_zoneid: ZonedDateTime
        get() = started_at.withZoneSameInstant(pattern.timezone)

    val started_at_local: ZonedDateTime
        get() = started_at.withZoneSameInstant(ZoneId.systemDefault())

    val ended_at_zoneid: ZonedDateTime
        get() = ended_at.withZoneSameInstant(pattern.timezone)

    val ended_at_local: ZonedDateTime
        get() = ended_at.withZoneSameInstant(ZoneId.systemDefault())

    fun setStartedAt(s: ZonedDateTime) {
        val newStartedAt = s.withZoneSameInstant(ZoneOffset.UTC)
        if (newStartedAt != started_at) {
            // todo check duration
            val d = Duration.between(started_at, newStartedAt)
            // update pattern started_at
            pattern.started_at = pattern.started_at.plus(d)
            started_at = newStartedAt
        }
    }

    fun setEndedAt(e: ZonedDateTime) {
        val newEndedAt = e.withZoneSameInstant(ZoneOffset.UTC)
        if (newEndedAt != ended_at) {
            val newDuration = Duration.between(started_at, newEndedAt)

            pattern.duration = newDuration
            ended_at = newEndedAt
        }
    }

    constructor(parcel: Parcel) :
            this(
                entity = parcel.readParcelable<EventServer>(EventServer::class.java.classLoader),
                pattern = parcel.readParcelable<EventPatternServer>(EventPatternServer::class.java.classLoader),
                started_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                ended_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!
            )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeParcelable(entity, 0)
        dest.writeParcelable(pattern, 0)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(started_at)!!)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(ended_at)!!)
    }

    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<EventInstance> {
        override fun createFromParcel(parcel: Parcel) = EventInstance(parcel)
        override fun newArray(size: Int): Array<EventInstance?> = arrayOfNulls(size)
    }
}