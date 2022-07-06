package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.MessageRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.validations.validateRequest
import io.github.xuenqui.eventosdarep.domain.services.MessageService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/events")
@Secured(SecurityRule.IS_AUTHENTICATED)
class MessageController(
    private val messageService: MessageService
) {

    @Post("/{eventId}/messages/user/{userId}/send")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun create(
        @PathVariable("eventId") eventId: String,
        @PathVariable("userId") userId: String,
        messageRequest: MessageRequest
    ): HttpResponse<Map<String, String>> {

        messageRequest.validateRequest()
        val id = messageService.create(messageRequest.text, userId, eventId)
        return HttpResponse.created(mapOf("id" to id))
    }

    @Get("/{eventId}/messages")
    fun getMessagesByEventId(
        @PathVariable("eventId") eventId: String
    ): List<MessageResponse> =
        messageService.findyByEventId(eventId).map { MessageResponse(it) }
}
