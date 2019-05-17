package com.example.calendar.data

import android.database.Cursor
import androidx.room.*
import com.example.calendar.data.oldEvent.EventTable
import io.reactivex.Completable
import io.reactivex.Flowable
import org.threeten.bp.ZonedDateTime
import java.util.*

const val queryFromTo = "SELECT * FROM eventsRecurrence " +
        "WHERE " +
        "(started_at >= :start and end_out_of_range < :end) " +
        "or (started_at < :end and end_out_of_range > :start)"

const val queryAddException =
    "INSERT INTO eventsRecurrenceException(event_id, exception_date) " +
            "SELECT :event_id, :exception_date " +
            "WHERE NOT EXISTS (" +
            "SELECT event_id, exception_date " +
            "FROM eventsRecurrenceException " +
            "WHERE event_id = :event_id " +
            "AND exception_date = :exception_date)"

@Dao
interface EventRecurrenceDao {

    @Query("SELECT * FROM eventsRecurrence WHERE id = :id")
    fun getEventById(id: String): List<EventRecurrence>

    @Query("SELECT * FROM eventsRecurrence WHERE id = :id")
    fun getEventByIdRx(id: String): Flowable<List<EventRecurrence>>


    @Insert
    fun insert(event: EventRecurrence): Void

    @Insert
    fun insertRx(event: EventRecurrence): Completable

    @Insert
    fun insertRx(event: List<EventRecurrence>): Completable


    @Update
    fun update(event: EventRecurrence): Void

    @Update
    fun updateRx(event: EventRecurrence): Completable


    @Delete
    fun delete(event: EventRecurrence): Void

    @Delete
    fun deleteRx(event: EventRecurrence): Completable


    // [from, to)
    @TypeConverters(ZoneDateTimeConverter::class)
    @Query(queryFromTo)
    fun fromTo(start: ZonedDateTime, end: ZonedDateTime): List<EventRecurrence>

    @TypeConverters(ZoneDateTimeConverter::class)
    @Query(queryFromTo)
    fun fromToRx(start: ZonedDateTime, end: ZonedDateTime): Flowable<List<EventRecurrence>>

    @Query(queryAddException)
    @TypeConverters(ZoneDateTimeConverter::class)
    fun addException(event_id: String, exception_date: ZonedDateTime)

    @Query("SELECT * FROM eventsRecurrenceException WHERE event_id = :eventId")
    fun getExceptionByEventId(eventId: String): List<EventRecurrenceException>

    @Query("DELETE FROM eventsRecurrenceException WHERE event_id = :eventId")
    fun deleteExceptionByEventId(eventId: String)

}