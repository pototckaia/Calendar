package com.example.calendar.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.calendar.helpers.getCalendarWithUTF
import java.util.*
import android.arch.persistence.room.Dao

@Entity(tableName = "events")
@TypeConverters(CalendarConverter::class)
data class EventTable(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "name") var name: String,

//Time in UTF
    @ColumnInfo(name = "started_at") var started_at: Calendar,
    @ColumnInfo(name = "ended_at") var ended_at: Calendar
)
