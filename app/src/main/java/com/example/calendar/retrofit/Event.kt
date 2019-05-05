package com.example.calendar.retrofit

data class Event(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val details: String,
    val status: String,
    val location: String)
