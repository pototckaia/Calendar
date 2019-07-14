package com.example.calendar

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import java.util.Collections.emptyList
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener


class FirebaseUIActivity : AppCompatActivity() {

    companion object {
        fun newInstance() : FirebaseUIActivity {
            return FirebaseUIActivity()
        }
    }

    private val RC_SIGN_IN = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)
        createSignInIntent()
    }

    fun createSignInIntent() {
        // Choose authentication providers
        val providers = Arrays.asList(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
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
                Log.d("User__", user.toString(), null)
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
    }

    fun delete() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
    }
//
//    fun themeAndLogo() {
//        val providers = Collections.emptyList()
//
//        startActivityForResult(
//            AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .setLogo(R.drawable.my_great_logo)      // Set logo drawable
//                .setTheme(R.style.MySuperAppTheme)      // Set theme
//                .build(),
//            RC_SIGN_IN
//        )
//    }
//
//    fun privacyAndTerms() {
//        val providers = Collections.emptyList()
//        // [START auth_fui_pp_tos]
//        startActivityForResult(
//            AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .setTosAndPrivacyPolicyUrls(
//                    "https://example.com/terms.html",
//                    "https://example.com/privacy.html"
//                )
//                .build(),
//            RC_SIGN_IN
//        )
//        // [END auth_fui_pp_tos]
//    }
}