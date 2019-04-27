package com.example.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calendar.navigation.CiceroneApplication
import com.example.calendar.navigation.Screens
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.android.support.SupportAppNavigator



class MainActivity : AppCompatActivity() {

    private var navigator = SupportAppNavigator(this, R.id.clMainContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // todo inject
            CiceroneApplication.instance.router.newRootScreen(Screens.NavigationScreen())
        }
    }

    override fun onResume() {
        super.onResume()
        // todo inject
        CiceroneApplication.instance.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        // todo inject
        CiceroneApplication.instance.navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        // todo inject
        CiceroneApplication.instance.router.exit()
    }

}