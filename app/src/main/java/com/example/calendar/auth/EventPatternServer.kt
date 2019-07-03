package com.example.calendar.auth


data class EventPattern(
    val id: Long?,
    val eventId: Event,
    val type: String,

    val year: String,
    val weekDay: String,
    val month: String,
    val day: String,
    val hour: String,
    val minute: String,

    val duration: Long
)