package com.example.calendar.inject

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Cicerone
import android.app.Application
import com.example.calendar.R
import com.example.calendar.customView.TimeZoneModel
import com.example.calendar.repository.db.EventRecurrenceDao
import com.example.calendar.repository.db.EventRecurrenceRepository
import com.example.calendar.repository.db.EventRoomDatabase
import com.example.calendar.repository.server.EventServerRepository
import com.example.calendar.repository.server.Server
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.ZoneId


class InjectApplication : Application() {

    companion object {
        lateinit var inject: InjectApplication
    }

    private lateinit var cicerone: Cicerone<Router>

    val navigatorHolder: NavigatorHolder
        get() = cicerone.navigatorHolder

    val router: Router
        get() = cicerone.router

    var timezone = emptyList<TimeZoneModel>()
    var timezoneName = HashMap<String, String>()

    private lateinit var server: Server
    lateinit var repository: EventServerRepository

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        cicerone = Cicerone.create()
        server = Server()
        repository = EventServerRepository(server.api)

//        dao = EventRoomDatabase.getInstance(applicationContext).eventRecurrenceDao()
//        repository = EventRecurrenceRepository(dao)
        resources.getStringArray(R.array.timezone)
            .forEach {
                val splitResult = it.split("\\|".toRegex(), 2).map { e -> e.trim() }
                timezoneName[splitResult[0]] = splitResult[1]
            }
        timezone  = timezoneName.map { TimeZoneModel(ZoneId.of(it.key), it.value) }

        inject = this
    }
}