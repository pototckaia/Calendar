package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.max
import com.example.calendar.helpers.toDateTimeUTC
import com.example.calendar.repository.db.convert.DurationConverter
import com.example.calendar.repository.db.convert.ZonedDateTimeConverter
import com.example.calendar.repository.db.convert.ZoneIdConverter
import com.example.calendar.repository.duration_cn
import com.example.calendar.repository.zoneId_cn
import com.example.calendar.repository.zonedDateTime_cn
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

    fun getPatternRequest() = PatternRequest(
        started_at = started_at,
        duration = duration,
        ended_at = ended_at,
        exrules = exrules.map { RruleStructure(it.rule) },
        rrule = rrule,
        timezone = timezone
    )

    constructor(parcel: Parcel) :
            this(
                id = parcel.readLong(),
                // todo a lof of create
                created_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                updated_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                started_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                duration = duration_cn.toDuration(parcel.readLong())!!,
                ended_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                exrules = emptyList<EventPatternExruleServer>(),
                rrule = parcel.readString()!!,
                timezone = zoneId_cn.toZoneId(parcel.readString())!!
            )
    {
        parcel.readTypedList(exrules, EventPatternExruleServer.CREATOR)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeLong(id)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(created_at)!!)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(updated_at)!!)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(started_at)!!)
        dest.writeLong(duration_cn.fromDuration(duration)!!)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(ended_at)!!)
        dest.writeString(rrule)
        dest.writeString(zoneId_cn.fromZoneId(timezone))
        dest.writeTypedList(exrules)
    }

    override fun describeContents() =  0
    companion object CREATOR : Parcelable.Creator<EventPatternServer> {
        override fun createFromParcel(parcel: Parcel) = EventPatternServer(parcel)
        override fun newArray(size: Int): Array<EventPatternServer?> = arrayOfNulls(size)
    }
}
