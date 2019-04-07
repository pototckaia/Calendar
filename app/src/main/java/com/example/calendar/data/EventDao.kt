package com.example.calendar.data

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*


@Dao
interface EventDao : EventRepository {

    @Query("SELECT * FROM events WHERE id = :id")
    override fun getUserById(id: String): Flowable<List<EventTable>>

    @Insert
    override fun insert(event: EventTable) : Completable

    @Update
    override fun update(event: EventTable) : Completable

    @Delete
    override fun delete(event: EventTable) : Completable

    @Query("DELETE FROM events")
    override fun deleteAll()

    @get:Query("SELECT * from events ORDER BY started_at ASC")
    override val all: Flowable<List<EventTable>>

    // [from, to)
    @TypeConverters(CalendarConverter::class)
    @Query("SELECT * FROM events WHERE (started_at >= :start and ended_at < :end) or (started_at < :end and ended_at > :start)")
    override fun fromTo(start: Calendar, end: Calendar): Flowable<List<EventTable>>

}