package com.example.calendar.data

import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*

interface EventRepository {

    fun getUserById(id: String): Flowable<EventTable>

    fun insert(event: EventTable) : Completable

    fun update(event: EventTable) : Completable

    fun deleteAll()

    val all: Flowable<List<EventTable>>

//    fun fromTo(start: Calendar, end: Calendar): List<EventTable>
//
//    val allLive: LiveData<List<EventTable>>
//
//    fun fromToLive(start: Calendar, end: Calendar): LiveData<List<EventTable>>
}