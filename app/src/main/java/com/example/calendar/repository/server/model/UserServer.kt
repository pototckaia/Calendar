package com.example.calendar.repository.server.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserServer(
    val id: String,
    val username: String
) : Parcelable