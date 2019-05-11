package com.example.calendar.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import java.util.*
@Entity(tableName = "eventsRecurrence")
@TypeConverters(ZoneDateTimeConverter::class, DurationConverter::class)
data class EventRecurrence(
    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "note") var note: String,

    // in UTF
    @ColumnInfo(name = "started_at") var startedAt: ZonedDateTime,
    // in minute
    @ColumnInfo(name = "duration") var duration: Duration,
    // for all recurrence event or endOutRecurrence for single event
    @ColumnInfo(name = "end_out_of_range") var endOutRecurrence: ZonedDateTime,

    @ColumnInfo(name = "recurrencePattern") var rrule: String = ""
) {

    init {
        startedAt = startedAt.withZoneSameInstant(ZoneOffset.UTC)
        endOutRecurrence = endOutRecurrence.withZoneSameInstant(ZoneOffset.UTC)
    }


    fun isRecurrence() : Boolean {
        return rrule.isNotEmpty()
    }
}

