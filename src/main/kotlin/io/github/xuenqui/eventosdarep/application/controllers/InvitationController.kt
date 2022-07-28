package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.InvitationRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.validations.validateRequest
import io.github.xuenqui.eventosdarep.domain.Invitation
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.domain.services.InvitationService
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/invitations")
//@Secured(SecurityRule.IS_AUTHENTICATED)
@Secured(SecurityRule.IS_ANONYMOUS)
class InvitationController(
    private val invitationService: InvitationService
) {

    @Post
    fun create(
        @Body invitationRequest: InvitationRequest
    ): HttpResponse<Map<String, String>> {
        logger.info("Request received to create a new invitation $invitationRequest")
        invitationRequest.validateRequest()

        val invitationId = invitationService.create(invitationRequest.email)

        return HttpResponse.created(mapOf("id" to invitationId)).also {
            logger.info("Invitation created with id $invitationId")
        }
    }

    @Get
    fun findByEmail(
        @QueryValue("email") email: String
    ): Invitation {
        logger.info("Request received to find an invitation by email $email")
        return invitationService.findByEmail(email)
            ?: throw ResourceNotFoundException("Invitation not found for email $email")
    }

    @Delete("/{email}")
    fun delete(
        @PathVariable("email") email: String
    ): HttpResponse<Nothing> {
        logger.info("Request received to delete an invitation by email $email")
        invitationService.delete(email)

        return HttpResponse.noContent()
    }

    companion object : LoggableClass()
}
