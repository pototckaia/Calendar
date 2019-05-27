package com.example.calendar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [EventRecurrence::class, EventRecurrenceException::class],
    version = 2, exportSchema = false
)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun eventRecurrenceDao() : EventRecurrenceDao

    companion object {

        @Volatile
        private var instant: EventRoomDatabase? = null

        fun getInstance(context: Context): EventRoomDatabase =
            instant ?: synchronized(this) {
                instant
                    ?: buildDatabase(context).also { instant = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                EventRoomDatabase::class.java, "event_recurrence.db"
            ).build()
    }
}