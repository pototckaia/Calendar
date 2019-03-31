package com.example.calendar.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*


@Dao
interface EventDao : EventRepository {

    @Query("SELECT * FROM events WHERE id = :id")
    override fun getUserById(id: String): Flowable<EventTable>

    @Insert
    override fun insert(event: EventTable) : Completable

    @Update
    override fun update(event: EventTable) : Completable

    @Query("DELETE FROM events")
    override fun deleteAll()

//    @TypeConverters(CalendarConverter::class)
//    @Query("SELECT * FROM events WHERE started_at >= :start OR ended_at <= :end")
//    override fun fromTo(start: Calendar, end: Calendar): List<EventTable>
//
//    @TypeConverters(CalendarConverter::class)
//    @Query("SELECT * FROM events WHERE started_at >= :start OR ended_at <= :end")
//    override fun fromToLive(start: Calendar, end: Calendar): LiveData<List<EventTable>>
//
//    @get:Query("SELECT * from events ORDER BY started_at ASC")
//    override val all: List<EventTable>
//
//    @get:Query("SELECT * from events ORDER BY started_at ASC")
//    override val allLive: LiveData<List<EventTable>>
}