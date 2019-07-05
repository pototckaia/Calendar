package com.example.calendar.server

open class NetworkException(message: String) : RuntimeException(message)

class NoContent : NetworkException("No content")

class BadRequest : NetworkException("Bad request")

class NotAuthorized : NetworkException("You are not authorized to view the resource")

class AccessDenied : NetworkException("Accessing the resource you were trying to reach is forbidden")

class NotFind : NetworkException("The resource you were trying to reach is not found")

class InternalError : NetworkException("Internal error")

