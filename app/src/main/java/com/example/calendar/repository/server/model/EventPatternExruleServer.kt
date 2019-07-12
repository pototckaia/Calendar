package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.repository.zonedDateTime_cn
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
                created_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!,
                updated_at = zonedDateTime_cn.toZonedDateTime(parcel.readLong())!!
            )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest == null) return

        dest.writeLong(id)
        dest.writeString(rule)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(created_at)!!)
        dest.writeLong(zonedDateTime_cn.fromZonedDateTime(updated_at)!!)
    }

    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<EventPatternExruleServer> {
        override fun createFromParcel(parcel: Parcel) = EventPatternExruleServer(parcel)
        override fun newArray(size: Int): Array<EventPatternExruleServer?> = arrayOfNulls(size)
    }
}
