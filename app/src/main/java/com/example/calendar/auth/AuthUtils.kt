package com.example.calendar.auth

import com.example.calendar.inject.InjectApplication
import com.example.calendar.navigation.Screens
import com.example.calendar.repository.server.model.UserServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

fun isFindCurrentUser() = FirebaseAuth.getInstance().currentUser != null

fun getCurrentFirebaseUser(): FirebaseUser {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        // No user is signed in
        // might not finished initializing
        InjectApplication.inject.router.newRootChain(Screens.NavigationScreen(), Screens.AuthScreen())
    }
    return user!!
}

fun isCurrentFirebaseUser(u : UserServer) = getCurrentFirebaseUser().uid == u.id