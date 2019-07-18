package com.example.calendar.repository.server.model

data class PermissionModel(
    val id: Long,
    val entity_id: String,
    val mine: Boolean,
    val entityType: EntityType,
    val entityName: String,
    val username: String,
    val actionType: PermissionAction,
    val isAll: Boolean
)
