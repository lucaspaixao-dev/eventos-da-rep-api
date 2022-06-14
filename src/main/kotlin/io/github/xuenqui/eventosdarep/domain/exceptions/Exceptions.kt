package io.github.xuenqui.eventosdarep.domain.exceptions

import io.micronaut.http.HttpStatus

open class ApiException(
    message: String,
    val code: HttpStatus,
    val details: Map<String, List<String>>? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)

class ValidationException(message: String, details: Map<String, List<String>>? = null) :
    ApiException(message, HttpStatus.BAD_REQUEST, details = details)

class ResourceAlreadyExistsException(message: String) : ApiException(message, HttpStatus.CONFLICT)

class ResourceNotFoundException(message: String) : ApiException(message, HttpStatus.NOT_FOUND)

class RepositoryException(message: String, throwable: Throwable) :
    ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR, throwable = throwable)
