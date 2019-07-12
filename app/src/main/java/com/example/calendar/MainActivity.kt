package com.example.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calendar.helpers.OnBackPressed
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import ru.terrakok.cicerone.android.support.SupportAppNavigator


class MainActivity : AppCompatActivity() {

    private var navigator = SupportAppNavigator(this, R.id.clMainContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // todo inject
            InjectApplication.inject.router.newRootScreen(Screens.NavigationScreen())
        }
    }

    override fun onResume() {
        super.onResume()
        // todo inject
        InjectApplication.inject.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        // todo inject
        InjectApplication.inject.navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        supportFragmentManager.executePendingTransactions()
        val fragment = supportFragmentManager.findFragmentById(R.id.clMainContainer) as? OnBackPressed

        if (fragment != null) {
            fragment.onBackPressed()
        } else {
            // todo inject
            InjectApplication.inject.router.exit()
        }
    }

}