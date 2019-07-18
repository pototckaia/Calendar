package com.example.calendar.repository.server.model

data class PermissionModel(
    val id: Long,
    val mine: Boolean,
    val entityType: EntityType,
    val entityName: String,
    val user_id: String,
    val username: String,
    val actionType: PermissionAction,
    val forAll: Boolean
)
