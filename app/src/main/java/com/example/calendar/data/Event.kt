package com.example.calendar.data

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.getCalendarWithUTF
import java.util.*

class Event(var text: String, var begin: Long, var end: Long) : Parcelable {

    constructor() : this("", 0, 0)

    var beginDate: Date
        get() = Date(begin)
        set(value) {
            begin = value.time
        }

    var endDate: Date
        get() = Date(end)
        set(value) {
            end = value.time
        }

    var beginCalendar: Calendar
        get() {
            val c = getCalendarWithUTF()
            c.timeInMillis = begin
            return c
        }
        set(value) {
            begin = value.timeInMillis
        }

    var endCalendar: Calendar
        get() {
            val c = getCalendarWithUTF()
            c.timeInMillis = end
            return c
        }
        set(value) {
            end = value.timeInMillis
        }


    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.let {
            it.writeString(text)
            it.writeLong(begin)
            it.writeLong(end)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(source: Parcel): Event {
            return Event(source)
        }

        override fun newArray(size: Int): Array<Event> {
            return Array(size, { Event() })
        }
    }

}