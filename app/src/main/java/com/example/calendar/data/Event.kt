package com.example.calendar.data

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Event(var text:String, var begin: Long, var end: Long) : Parcelable {

    constructor(text: String) : this(text, 0, 0) {
        val startDate = Calendar.getInstance()
        startDate.set(Calendar.HOUR, 1)
        startDate.set(Calendar.MINUTE, 0)
        startDate.set(Calendar.SECOND, 0)
        val endDate = startDate.clone() as Calendar
        endDate.set(Calendar.HOUR, 2)
        begin = startDate.timeInMillis
        end = endDate.timeInMillis
    }

    constructor() : this("", 0, 0)

    var beginDate: Date
        get() = Date(begin)
        set(value) { begin = value.time }

    var endDate: Date
        get() = Date(end)
        set(value) { end = value.time }

    var beginCalendar: Calendar
        get() {
            val c = Calendar.getInstance()
            c.timeInMillis = begin
            return c
        }
        set(value) { begin = value.timeInMillis }

    var endCalendar: Calendar
        get() {
            val c = Calendar.getInstance()
            c.timeInMillis = end
            return c
        }
        set(value) { end = value.timeInMillis }


    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong())

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
            return Array(size, {Event()} )
        }
    }

}