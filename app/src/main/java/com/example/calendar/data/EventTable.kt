package com.example.calendar.data

import android.arch.persistence.room.*
import java.util.*

@Entity(tableName = "events")
@TypeConverters(CalendarConverter::class)
data class EventTable(
    @PrimaryKey(autoGenerate = true) var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,

//Time in UTF
    @ColumnInfo(name = "started_at") var started_at: Calendar,
    @ColumnInfo(name = "ended_at") var ended_at: Calendar
)
