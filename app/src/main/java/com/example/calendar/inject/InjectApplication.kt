package com.example.calendar.inject

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Cicerone
import android.app.Application
import com.example.calendar.repository.db.EventRecurrenceDao
import com.example.calendar.repository.db.EventRecurrenceRepository
import com.example.calendar.repository.db.EventRoomDatabase
import com.example.calendar.repository.server.EventServerRepository
import com.example.calendar.repository.server.Server
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

    private lateinit var server: Server
    lateinit var repository: EventServerRepository

    override fun onCreate() {
        super.onCreate()
        cicerone = Cicerone.create()
        server = Server()
        repository = EventServerRepository(server.api)

//        dao = EventRoomDatabase.getInstance(applicationContext).eventRecurrenceDao()
//        repository = EventRecurrenceRepository(dao)
        inject = this
        AndroidThreeTen.init(this);
    }
}