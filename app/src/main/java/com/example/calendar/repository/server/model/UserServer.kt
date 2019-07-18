package com.example.calendar.repository.server.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class UserModel(
    val id: String,
    val username: String,
    val photo: String?,

    val enabled: Boolean,
    val credentials_non_expired: Boolean,
    val account_non_locked: Boolean,
    val account_non_expired: Boolean
)

@Parcelize
data class UserServer(
    val id: String,
    val username: String
) : Parcelable