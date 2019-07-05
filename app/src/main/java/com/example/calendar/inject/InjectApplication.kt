package com.example.calendar.inject

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Cicerone
import android.app.Application
import com.example.calendar.repository.db.EventRecurrenceDao
import com.example.calendar.repository.db.EventRecurrenceRepository
import com.example.calendar.repository.db.EventRoomDatabase
import com.jakewharton.threetenabp.AndroidThreeTen


class InjectApplication : Application() {

    companion object {
        lateinit var inject: InjectApplication
    }

    private lateinit var cicerone: Cicerone<Router>

    val navigatorHolder: NavigatorHolder
        get() = cicerone.navigatorHolder

    val router: Router
        get() = cicerone.router

    lateinit var dao: EventRecurrenceDao
    lateinit var repository: EventRecurrenceRepository

    override fun onCreate() {
        super.onCreate()
        cicerone = Cicerone.create()
        inject = this
        dao = EventRoomDatabase.getInstance(applicationContext).eventRecurrenceDao()
        repository = EventRecurrenceRepository(dao)
        AndroidThreeTen.init(this);
    }
}