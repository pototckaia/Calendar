package com.example.calendar.retrofit

data class Task(
    val id: Long?,
    val eventId: Long,
    val parentId: Long,
    val name: String,
    val details: String,
    val status: String
)