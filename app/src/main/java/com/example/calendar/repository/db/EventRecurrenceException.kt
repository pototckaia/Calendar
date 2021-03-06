package com.example.calendar.repository.db

import androidx.room.*
import com.example.calendar.helpers.convert.ZonedDateTimeConverter
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
@TypeConverters(ZonedDateTimeConverter::class)
data class EventRecurrenceException(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "event_id") var eventId: String,
    @ColumnInfo(name = "exception_date") var exceptionDate: ZonedDateTime
)
