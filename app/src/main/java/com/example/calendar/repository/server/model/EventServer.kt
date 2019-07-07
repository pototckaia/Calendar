package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.toLongUTC
import org.threeten.bp.ZonedDateTime

data class EventServer(
    val id: Long,
    val owner_id: Long,

    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime,

    var name: String,
    var details: String,
    var status: String,
    var location: String
) : Parcelable {

    constructor(parcel: Parcel) :
            this(
                id = parcel.readLong(),
                owner_id = parcel.readLong(),
                created_at = fromLongUTC(parcel.readLong()),
                updated_at = fromLongUTC(parcel.readLong()),
                name = parcel.readString()!!,
                details = parcel.readString()!!,
                status = parcel.readString()!!,
                location = parcel.readString()!!
            )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeLong(id)
        dest.writeLong(owner_id)
        dest.writeLong(toLongUTC(created_at))
        dest.writeLong(toLongUTC(updated_at))
        dest.writeString(name)
        dest.writeString(details)
        dest.writeString(status)
        dest.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventServer> {
        override fun createFromParcel(parcel: Parcel): EventServer {
            return EventServer(parcel)
        }

        override fun newArray(size: Int): Array<EventServer?> {
            return arrayOfNulls(size)
        }
    }
}
