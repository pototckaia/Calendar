package com.example.calendar.data

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.content.Context
import android.arch.persistence.room.Room


@Database(entities = [EventTable::class], version = 1, exportSchema = false)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var instance: EventRoomDatabase? = null

        fun getDatabase(context: Context): EventRoomDatabase? {
            if (instance == null) {
                synchronized(EventRoomDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            EventRoomDatabase::class.java, "event_database"
                        )
                            .build()
                    }
                }
            }
            return instance
        }
    }
}