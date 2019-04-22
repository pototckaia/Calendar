package com.example.calendar.data

import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*

interface EventRepository {

    fun getUserById(id: String): Flowable<List<EventTable>>

    fun insert(event: EventTable): Completable

    fun update(event: EventTable): Completable

    fun delete(event: EventTable): Completable

    fun deleteAll()

    val all: Flowable<List<EventTable>>

    fun fromTo(start: Calendar, end: Calendar): Flowable<List<EventTable>>

}