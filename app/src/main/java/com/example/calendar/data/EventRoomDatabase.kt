package com.example.calendar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calendar.data.oldEvent.EventDao
import com.example.calendar.data.oldEvent.EventTable


@Database(
    entities = [EventRecurrence::class, EventRecurrenceException::class, EventTable::class],
    version = 1, exportSchema = false
)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

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
                EventRoomDatabase::class.java, "event.db"
            ).build()
    }
}