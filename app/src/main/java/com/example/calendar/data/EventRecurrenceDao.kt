package com.example.calendar.data

import androidx.room.*
import org.threeten.bp.ZonedDateTime
import java.util.*


@Dao
interface EventRecurrenceDao {

    @Query("SELECT * FROM eventsRecurrence WHERE id = :id")
    fun getEventById(id: String): EventRecurrence

    @Insert
    fun insert(event: EventRecurrence): Void

    @Update
    fun update(event: EventRecurrence): Void

    @Delete
    fun delete(event: EventRecurrence): Void

    @Query("DELETE FROM eventsRecurrence")
    fun deleteAll() : Void

    // [from, to)
    @TypeConverters(ZoneDateTimeConverter::class)
    @Query("SELECT * FROM eventsRecurrence " +
            "WHERE " +
            "(started_at >= :start and end_out_of_range < :end) " +
            "or (started_at < :end and end_out_of_range > :start)")
    fun fromTo(start: ZonedDateTime, end: ZonedDateTime): List<EventRecurrence>

    @Query("INSERT INTO eventsRecurrenceException " +
        "WHERE NOT EXISTS (" +
        "SELECT * FROM eventsRecurrenceException " +
            "WHERE event_id = :event_id " +
            "AND exception_date = :exception_date)")
    fun addException(event_id: String, exception_date: ZonedDateTime) : Void

    @Query("SELECT * FROM eventsRecurrenceException WHERE event_id = :eventId")
    fun getExceptionByEventId(eventId: String) : List<EventRecurrenceException>

    @Query("DELETE FROM eventsRecurrenceException WHERE event_id = :eventId")
    fun deleteExceptionByEventId(eventId: String) : Void

}