package com.example.calendar.repository.server.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime

@Parcelize
data class EventPatternExruleServer(
    val id: Long,

    var rule: String,
    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime
) : Parcelable
