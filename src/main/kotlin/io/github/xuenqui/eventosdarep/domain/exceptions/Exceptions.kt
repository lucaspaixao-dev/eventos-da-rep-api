package io.github.xuenqui.eventosdarep.domain.exceptions

import io.micronaut.http.HttpStatus

open class ApiException(
    message: String,
    val code: HttpStatus,
    val details: List<String>? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)

class ValidationException(message: String, details: List<String>? = null) :
    ApiException(message, HttpStatus.BAD_REQUEST, details = details)

class ResourceAlreadyExistsException(message: String) : ApiException(message, HttpStatus.CONFLICT)

class ResourceNotFoundException(message: String) : ApiException(message, HttpStatus.NOT_FOUND)

class RepositoryException(message: String, throwable: Throwable) :
    ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR, throwable = throwable)

class UserNotInvitedException(message: String) : ApiException(message, HttpStatus.FORBIDDEN)
