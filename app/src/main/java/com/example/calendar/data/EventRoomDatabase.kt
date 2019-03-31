package com.example.calendar.data

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.content.Context
import android.arch.persistence.room.Room


@Database(entities = [EventTable::class], version = 1, exportSchema = false)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {

        @Volatile private var INSTANCE: EventRoomDatabase? = null

        fun getInstance(context: Context): EventRoomDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                EventRoomDatabase::class.java, "event.db")
                .build()
    }
}