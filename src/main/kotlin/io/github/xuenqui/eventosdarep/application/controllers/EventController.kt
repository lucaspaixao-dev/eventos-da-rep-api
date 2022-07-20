package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.CreateNotificationRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.EventRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.toDomain
import io.github.xuenqui.eventosdarep.application.controllers.requests.validations.validateRequest
import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.services.EventService
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/events")
class EventController(
    private val eventService: EventService
) {

    @Post
    fun create(
        @Body eventRequest: EventRequest
    ): HttpResponse<Map<String, String>> {
        logger.info("Request received to create a new event, $eventRequest")

        eventRequest.validateRequest()
        val domain = eventRequest.toDomain()
        val uuid = eventService.create(domain)

        return HttpResponse.created(mapOf("id" to uuid)).also {
            logger.info("Event created, $uuid")
        }
    }

    @Get
    fun getAll(
        @QueryValue(value = "page", defaultValue = "0") page: String,
        @QueryValue(value = "size", defaultValue = "20") size: String
    ): List<Event> = eventService.findAll(page.toInt(), size.toInt())

    @Get("/actives")
    fun getActiveEvents(
        @QueryValue(value = "page", defaultValue = "0") page: String,
        @QueryValue(value = "size", defaultValue = "20") size: String
    ): List<Event> = eventService.findActiveEvents(page.toInt(), size.toInt())

    @Get("/{id}")
    fun getById(@PathVariable("id") id: String): Event = eventService.findById(id)

    @Put("/{id}")
    fun update(
        @PathVariable("id") id: String,
        eventRequest: EventRequest
    ): Event {
        logger.info("Request received to update event, $eventRequest")

        eventRequest.validateRequest()
        val domain = eventRequest.toDomain()
        return eventService.update(id, domain).also {
            logger.info("Event updated, $it")
        }
    }

    @Put("/{eventId}/users/{userId}/accept")
    fun join(
        @PathVariable("eventId") eventId: String,
        @PathVariable("userId") userId: String
    ): HttpResponse<Void> {
        logger.info("Request received to join event, $eventId, $userId")
        eventService.join(eventId, userId)
        return HttpResponse.noContent<Void?>().also {
            logger.info("User joined event, $eventId, $userId")
        }
    }

    @Put("/{eventId}/users/{userId}/disavow")
    fun remove(
        @PathVariable("eventId") eventId: String,
        @PathVariable("userId") userId: String
    ): HttpResponse<Void> {
        logger.info("Request received to remove user from event, $eventId, $userId")
        eventService.remove(eventId, userId)
        return HttpResponse.noContent<Void?>().also {
            logger.info("User removed from event, $eventId, $userId")
        }
    }

    @Post("/{eventId}/notifications")
    fun sendNotification(
        @PathVariable("eventId") eventId: String,
        notificationRequest: CreateNotificationRequest
    ): HttpResponse<Void> {
        logger.info("Request received to send notification to event, $eventId, $notificationRequest")
        eventService.sendNotification(eventId, notificationRequest.title, notificationRequest.message)

        return HttpResponse.noContent<Void?>().also {
            logger.info("Notification sent to event, $eventId")
        }
    }

    companion object : LoggableClass()
}
