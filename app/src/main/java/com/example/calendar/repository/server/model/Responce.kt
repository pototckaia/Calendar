package com.example.calendar.repository.server.model

data class EventResponse(
    val count: Int,
    val data: List<EventServer>,
    val message: String,
    val offset: Long,
    val status: Int,
    val success: Boolean
)

data class EventInstanceResponse(
    val count: Int,
    val data: List<EventInstanceServer>,
    val message: String,
    val offset: Long,
    val status: Int,
    val success: Boolean
)

data class EventPatternResponse(
    val count: Int,
    val data: List<EventPatternServer>,
    val message: String,
    val offset: Long,
    val status: Int,
    val success: Boolean
)

data class PermissionResponse(
    val count: Int,
    val data: List<PermissionServer>,
    val message: String,
    val offset: Long,
    val status: Int,
    val success: Boolean
)

data class TaskResponse(
    val count: Int,
    val data: List<TaskServer>,
    val message: String,
    val offset: Long,
    val status: Int,
    val success: Boolean
)

data class UserResponse(
    val id: String,
    val username: String,
    val photo: String,

    val enabled: Boolean,
    val credentials_non_expired: Boolean,
    val account_non_locked: Boolean,
    val account_non_expired: Boolean   
)