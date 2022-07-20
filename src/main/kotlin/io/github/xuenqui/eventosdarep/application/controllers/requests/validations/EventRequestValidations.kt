package io.github.xuenqui.eventosdarep.application.controllers.requests.validations

import io.github.xuenqui.eventosdarep.application.controllers.requests.EventRequest
import io.github.xuenqui.eventosdarep.domain.exceptions.ValidationException
import java.util.Locale
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.hasSize
import org.valiktor.functions.isLessThan
import org.valiktor.functions.isNotBlank
import org.valiktor.i18n.mapToMessage
import org.valiktor.validate

fun EventRequest.validateRequest() {
    try {
        validate(this) {
            validate(EventRequest::title).isNotBlank()
            validate(EventRequest::title).hasSize(min = 5, max = 30)
            validate(EventRequest::description).isNotBlank()
            validate(EventRequest::description).hasSize(min = 5)
            validate(EventRequest::latitude).isLessThan(0.0)
            validate(EventRequest::longitude).isLessThan(0.0)
            validate(EventRequest::address).isNotBlank()
            validate(EventRequest::address).hasSize(min = 5, max = 100)
            validate(EventRequest::city).isNotBlank()
            validate(EventRequest::city).hasSize(min = 5, max = 50)
            validate(EventRequest::photo).isNotBlank()
//            validate(EventRequest::date).isNotNull()
//            validate(EventRequest::date).isGreaterThan(LocalDate.now())
//            validate(EventRequest::begin).isNotNull()
//            validate(EventRequest::end).isNotNull()
        }
    } catch (e: ConstraintViolationException) {
        val errors = e.constraintViolations
            .mapToMessage(baseName = "errors", locale = Locale.ENGLISH)
            .map { "${it.property}: ${it.message}" }

        throw ValidationException("Errors found", details = errors)
    }
}
