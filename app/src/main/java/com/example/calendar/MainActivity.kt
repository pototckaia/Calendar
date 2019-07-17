package com.example.calendar

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.calendar.auth.getCurrentFirebaseUser
import com.example.calendar.helpers.OnBackPressed
import com.example.calendar.helpers.USER_ID_TOKEN
import com.example.calendar.helpers.USER_ID_TOKEN_PREF
import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private var navigator = SupportAppNavigator(this, R.id.clMainContainer)

    private var isNew = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UserServer is signed in
        // token might not valid : local token has not refreshed
        getCurrentFirebaseUser().getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    InjectApplication.inject
                        .getSharedPreferences(USER_ID_TOKEN_PREF, Context.MODE_PRIVATE)
                        .edit()
                        .putString(USER_ID_TOKEN, idToken)
                        .apply()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }


        if (savedInstanceState == null) {
            InjectApplication.inject.router.newRootScreen(Screens.NavigationScreen())
        }
    }

    override fun onResume() {
        super.onResume()
        InjectApplication.inject.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        InjectApplication.inject.navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        supportFragmentManager.executePendingTransactions()
        val fragment = supportFragmentManager.findFragmentById(R.id.clMainContainer) as? OnBackPressed

        if (fragment != null) {
            fragment.onBackPressed()
        } else {
            InjectApplication.inject.router.exit()
        }
    }
}