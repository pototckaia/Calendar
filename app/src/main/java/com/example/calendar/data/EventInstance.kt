package com.example.calendar.data

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.toLongUTC
import com.example.calendar.helpers.toTimeZone
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import java.util.*
import javax.security.auth.login.LoginException

data class EventInstance(
    val idEventRecurrence: String,
    var nameEventRecurrence: String,
    var noteEventRecurrence: String,
    // UTF
    private var startedAtInstance: ZonedDateTime,
    private var startedAtNotUpdate: ZonedDateTime,
    var zoneId: ZoneId,
    // min minute
    var duration: Duration,
    var rrule: String = ""
) : Parcelable {

    init {
        startedAtInstance = startedAtInstance.withZoneSameInstant(ZoneOffset.UTC)
        startedAtNotUpdate = startedAtNotUpdate.withZoneSameInstant(ZoneOffset.UTC)
    }

    val startedAtLocal: ZonedDateTime
        get() = startedAtInstance.withZoneSameInstant(zoneId)

    var startedAtLocalNotUpdate: ZonedDateTime
        get() = startedAtNotUpdate.withZoneSameInstant(zoneId)
        set(s: ZonedDateTime) {
            startedAtNotUpdate = s.withZoneSameInstant(ZoneOffset.UTC)
        }

    val endedAtLocal: ZonedDateTime
        get() = startedAtInstance.plus(duration)
            .withZoneSameInstant(zoneId)


    constructor(e: EventRecurrence, start: ZonedDateTime) :
            this(
                idEventRecurrence = e.id,
                nameEventRecurrence = e.name,
                noteEventRecurrence = e.note,
                startedAtInstance = start,
                startedAtNotUpdate = start,
                zoneId = e.zoneId,
                duration = e.duration,
                rrule = e.rrule
            ) {
        if (start.zone != e.zoneId) {
            // todo fix
            throw LoginException("different time zone")
        }
    }

    fun isRecurrence() = rrule.isNotEmpty()

    constructor(parcel: Parcel) :
            this(
                idEventRecurrence = parcel.readString()!!,
                nameEventRecurrence = parcel.readString()!!,
                noteEventRecurrence = parcel.readString()!!,
                startedAtInstance = fromLongUTC(parcel.readLong()),
                startedAtNotUpdate = fromLongUTC(parcel.readLong()),
                zoneId = ZoneId.of(parcel.readString()),
                duration = DurationConverter().toDuration(parcel.readLong())!!,
                rrule = parcel.readString()!!
            )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        val dz = DurationConverter()

        dest.writeString(idEventRecurrence)
        dest.writeString(nameEventRecurrence)
        dest.writeString(noteEventRecurrence)
        dest.writeLong(toLongUTC(startedAtInstance))
        dest.writeLong(toLongUTC(startedAtNotUpdate))
        dest.writeString(zoneId.toString())
        dest.writeLong(dz.fromDuration(duration)!!)
        dest.writeString(rrule)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventInstance> {
        override fun createFromParcel(parcel: Parcel): EventInstance {
            return EventInstance(parcel)
        }

        override fun newArray(size: Int): Array<EventInstance?> {
            return arrayOfNulls(size)
        }
    }
}


