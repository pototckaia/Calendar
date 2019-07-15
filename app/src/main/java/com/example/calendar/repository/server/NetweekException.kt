package com.example.calendar.repository.server

open class NetworkException(message: String) : RuntimeException(message)

class NoContent(val s: String) : NetworkException("No content: $s")

class BadRequest(val s: String) : NetworkException("Bad request: $s")

class NotAuthorized : NetworkException("You are not authorized to view the resource")

class AccessDenied : NetworkException("Accessing the resource you were trying to reach is forbidden")

class NotFind : NetworkException("The resource you were trying to reach is not found")

class InternalError : NetworkException("Internal error")

