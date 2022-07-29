package io.github.xuenqui.eventosdarep.application.controllers.requests.validations

import io.github.xuenqui.eventosdarep.application.controllers.requests.InvitationRequest
import io.github.xuenqui.eventosdarep.domain.exceptions.ValidationException
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import org.valiktor.i18n.mapToMessage
import org.valiktor.validate
import java.util.Locale

fun InvitationRequest.validateRequest() {
    try {
        validate(this) {
            validate(InvitationRequest::email).isNotBlank()
            validate(InvitationRequest::email).hasSize(min = 3, max = 100)
            validate(InvitationRequest::email).isEmail()
        }
    } catch (e: ConstraintViolationException) {
        val errors = e.constraintViolations
            .mapToMessage(baseName = "errors", locale = Locale.ENGLISH)
            .map { "${it.property}: ${it.message}" }

        throw ValidationException("Errors found", details = errors)
    }
}
