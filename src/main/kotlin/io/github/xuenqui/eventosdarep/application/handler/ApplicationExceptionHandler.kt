package io.github.xuenqui.eventosdarep.application.handler

import io.github.xuenqui.eventosdarep.domain.exceptions.ApiException
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Produces
@Singleton
@Requires(classes = [ApiException::class, ExceptionHandler::class])
class ApplicationExceptionHandler :
    ExceptionHandler<ApiException?, HttpResponse<ExceptionResponse>> {

    override fun handle(request: HttpRequest<*>, exception: ApiException?): HttpResponse<ExceptionResponse> {
        logger.error("An error occurred ${exception?.message}")

        val exceptionResponse = ExceptionResponse(
            message = exception?.message ?: "An error occurred",
            details = exception?.details
        )
        return HttpResponse
            .status<HttpStatus>(exception?.code ?: HttpStatus.INTERNAL_SERVER_ERROR)
            .body(exceptionResponse)
    }

    companion object : LoggableClass()
}
