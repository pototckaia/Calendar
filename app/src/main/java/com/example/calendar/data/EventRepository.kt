package com.example.calendar.data

import android.arch.lifecycle.LiveData
import java.util.*

interface EventRepository {

    val all: List<EventTable>

    fun insert(event: EventTable)

    fun update(event: EventTable)

    fun deleteAll()

    fun fromTo(start: Calendar, end: Calendar): List<EventTable>

    val allLive: LiveData<List<EventTable>>

    fun fromToLive(start: Calendar, end: Calendar): LiveData<List<EventTable>>
}