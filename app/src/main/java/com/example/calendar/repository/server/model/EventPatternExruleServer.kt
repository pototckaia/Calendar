package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.repository.db.convert.ZoneDateTimeConverter
import org.threeten.bp.ZonedDateTime

data class EventPatternExruleServer(
    val id: Long,

    var rule: String,
    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime
) : Parcelable {

    constructor(parcel: Parcel) :
            this(
                id = parcel.readLong(),
                rule = parcel.readString()!!,
                // todo a lof of create
                created_at = ZoneDateTimeConverter().toZoneDateTime(parcel.readLong())!!,
                updated_at = ZoneDateTimeConverter().toZoneDateTime(parcel.readLong())!!
            )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeLong(id)
        dest.writeString(rule)
        dest.writeLong(ZoneDateTimeConverter().fromZoneDateTime(created_at)!!)
        dest.writeLong(ZoneDateTimeConverter().fromZoneDateTime(updated_at)!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventPatternExruleServer> {
        override fun createFromParcel(parcel: Parcel): EventPatternExruleServer {
            return EventPatternExruleServer(parcel)
        }

        override fun newArray(size: Int): Array<EventPatternExruleServer?> {
            return arrayOfNulls(size)
        }
    }
}
