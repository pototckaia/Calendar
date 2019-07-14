package com.example.calendar.auth

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import android.widget.Toast
import com.example.calendar.inject.InjectApplication
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import java.util.*
import android.content.Context
import com.example.calendar.R
import com.example.calendar.helpers.USER_ID_TOKEN
import com.example.calendar.helpers.USER_ID_TOKEN_PREF


class FirebaseSignInActivity : AppCompatActivity() {

    companion object {
        fun newInstance() : FirebaseSignInActivity {
            return FirebaseSignInActivity()
        }
    }

    private val RC_SIGN_IN = 123

    private var navigator = SupportAppNavigator(this, R.id.llAuthContainer)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)
        createSignInIntent()
    }

    fun createSignInIntent() {
        val providers = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
//                .setLogo(R.drawable.my_great_logo)      // Set logo drawable
//                .setTheme(R.style.MySuperAppTheme)      // Set theme
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                user!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val idToken = task.result!!.token
                            InjectApplication.inject
                                .getSharedPreferences(USER_ID_TOKEN_PREF, Context.MODE_PRIVATE)
                                .edit()
                                .putString(USER_ID_TOKEN, idToken)
                                .apply()

                            InjectApplication.inject.router.exit()
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Sign in failed
                if (response == null) {
//                    the user canceled the sign-in flow using the back button
                    createSignInIntent()
                    return
                }
                Toast.makeText(this, response.error!!.errorCode, Toast.LENGTH_SHORT).show()
                // response.getError().getErrorCode() and handle the error.
            }
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
}