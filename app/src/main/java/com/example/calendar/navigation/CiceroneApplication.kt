package com.example.calendar.navigation

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Cicerone
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen


class CiceroneApplication : Application() {

    companion object {
        lateinit var instance: CiceroneApplication
    }

    private lateinit var cicerone: Cicerone<Router>

    val navigatorHolder: NavigatorHolder
        get() = cicerone.navigatorHolder

    val router: Router
        get() = cicerone.router

    override fun onCreate() {
        super.onCreate()
        cicerone = Cicerone.create()
        instance = this
        AndroidThreeTen.init(this);
    }
}