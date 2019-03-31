package com.example.calendar.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import java.util.*


@Dao
interface EventDao {

    @get:Query("SELECT * from events ORDER BY started_at ASC")
    val allWords: List<EventTable>

    @Insert
    fun insert(event: EventTable)

    @Update
    fun update(event: EventTable)

    @Query("DELETE FROM events")
    fun deleteAll()

    @TypeConverters(CalendarConverter::class)
    @Query("SELECT * FROM events WHERE started_at >= :start OR ended_at <= :end")
    fun fromTo(start: Calendar, end: Calendar): List<EventTable>


    @get:Query("SELECT * from events ORDER BY started_at ASC")
    val allWordsLive: LiveData<List<EventTable>>

    @TypeConverters(CalendarConverter::class)
    @Query("SELECT * FROM events WHERE started_at >= :start OR ended_at <= :end")
    fun fromToLive(start: Calendar, end: Calendar): LiveData<List<EventTable>>
}