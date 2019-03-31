package com.example.calendar.repository

import android.os.AsyncTask
import android.arch.lifecycle.LiveData
import android.app.Application
import com.example.calendar.data.EventDao
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.data.EventTable


class EventRepository internal constructor(application: Application) {

    private val eventDao: EventDao
    internal val allEvents: LiveData<List<EventTable>>

    init {
        val db = EventRoomDatabase.getDatabase(application)
        eventDao = db!!.eventDao()
        allEvents = eventDao.allWordsLive
    }

    fun insert(event: EventTable) {
        insertAsyncTask(eventDao).execute(event)
    }

    companion object {
        private class insertAsyncTask internal constructor(private val mAsyncTaskEvent: EventDao) :
            AsyncTask<EventTable, Void, Void>() {

            override fun doInBackground(vararg params: EventTable): Void? {
                mAsyncTaskEvent.insert(params[0])
                return null
            }
        }
    }
}