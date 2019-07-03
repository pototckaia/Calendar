package com.example.calendar.auth



data class Event(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val details: String,
    val status: String,
    val location: String)
