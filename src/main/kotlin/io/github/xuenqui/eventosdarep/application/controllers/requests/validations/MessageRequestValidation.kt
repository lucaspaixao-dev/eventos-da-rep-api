package io.github.xuenqui.eventosdarep.application.controllers.requests.validations

import io.github.xuenqui.eventosdarep.application.controllers.requests.MessageRequest
import io.github.xuenqui.eventosdarep.domain.exceptions.ValidationException
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.isNotBlank
import org.valiktor.i18n.mapToMessage
import org.valiktor.validate
import java.util.Locale

fun MessageRequest.validateRequest() {
    try {
        validate(this) {
            validate(MessageRequest::userId).isNotBlank()
            validate(MessageRequest::eventId).isNotBlank()
            validate(MessageRequest::text).isNotBlank()
        }
    } catch (e: ConstraintViolationException) {
        val errors = e.constraintViolations
            .mapToMessage(baseName = "errors", locale = Locale.ENGLISH)
            .map { "${it.property}: ${it.message}" }

        throw ValidationException("Errors found", details = errors)
    }
}
