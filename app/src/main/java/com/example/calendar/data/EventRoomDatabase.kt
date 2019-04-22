package com.example.calendar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [EventTable::class], version = 1, exportSchema = false)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {

        @Volatile
        private var instant: EventRoomDatabase? = null

        fun getInstance(context: Context): EventRoomDatabase =
            instant ?: synchronized(this) {
                instant ?: buildDatabase(context).also { instant = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                EventRoomDatabase::class.java, "event.db"
            ).build()
    }
}