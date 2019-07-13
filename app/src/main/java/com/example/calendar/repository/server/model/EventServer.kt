package com.example.calendar.repository.server.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime

@Parcelize
data class EventServer(
    val id: Long,
    val owner_id: Long,

    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime,

    var name: String,
    var details: String,
    var status: String,
    var location: String
) : Parcelable {

    var eventRequest: EventRequest
        get() {
            // error kotlin
            if (name == null) {
                name = ""
            }
            if (status == null) {
                status = ""
            }
            if (location == null) {
                location = ""
            }
            if (details == null) {
                details = ""
            }
            return EventRequest(name = name, details = details, status = status, location = location)
        }
        set(value) {
            name = value.name
            details = value.details
            status = value.status
            location = value.location
        }
}


