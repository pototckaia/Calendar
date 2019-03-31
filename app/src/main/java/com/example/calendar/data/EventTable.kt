package com.example.calendar.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "events")
@TypeConverters(CalendarConverter::class)
data class EventTable(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,

//Time in UTF
    @ColumnInfo(name = "started_at") var started_at: Calendar,
    @ColumnInfo(name = "ended_at") var ended_at: Calendar
)
