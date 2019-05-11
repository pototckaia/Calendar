package com.example.calendar.data

import androidx.room.*
import org.threeten.bp.ZonedDateTime
import java.util.UUID


@Entity(
    tableName = "eventsRecurrenceException",
    foreignKeys = [ForeignKey(
        entity = EventRecurrence::class,
        parentColumns = ["id"],
        childColumns = ["event_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value=["event_id"])]
)
@TypeConverters(ZoneDateTimeConverter::class)
data class EventRecurrenceException(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "event_id") var eventId: String,
    @ColumnInfo(name = "exception_date") var exceptionDate: ZonedDateTime
)
