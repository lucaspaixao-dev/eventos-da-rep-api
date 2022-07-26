package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.MessageRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.validations.validateRequest
import io.github.xuenqui.eventosdarep.application.controllers.responses.MessageResponse
import io.github.xuenqui.eventosdarep.domain.services.MessageService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/messages")
@Secured(SecurityRule.IS_AUTHENTICATED)
class MessageController(
    private val messageService: MessageService
) {

    @Post
    fun create(
        @Body messageRequest: MessageRequest
    ): HttpResponse<Map<String, String>> {
        messageRequest.validateRequest()
        val id = messageService.create(messageRequest.text, messageRequest.userId, messageRequest.eventId)
        return HttpResponse.created(mapOf("id" to id))
    }

    @Get
    fun getMessagesByEventId(
        @QueryValue("eventId") eventId: String
    ): List<MessageResponse> =
        messageService.findyByEventId(eventId).map { MessageResponse(it) }
}
