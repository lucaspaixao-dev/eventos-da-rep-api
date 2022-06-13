package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.CreateNotificationRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.EventRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.toDomain
import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.services.EventService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue

@Controller("/events")
class EventController(
    private val eventService: EventService
) {

    @Post
    fun create(eventRequest: EventRequest): HttpResponse<Map<String, String>> {
        val domain = eventRequest.toDomain()
        val uuid = eventService.create(domain)

        return HttpResponse.created(mapOf("id" to uuid))
    }

    @Get
    fun getAll(
        @QueryValue(value = "page", defaultValue = "0") page: String,
        @QueryValue(value = "size", defaultValue = "20") size: String,
    ): List<Event> = eventService.findAll(page.toInt(), size.toInt())

    @Get("/actives")
    fun getActiveEvents(
        @QueryValue(value = "page", defaultValue = "0") page: String,
        @QueryValue(value = "size", defaultValue = "20") size: String,
    ): List<Event> = eventService.findActiveEvents(page.toInt(), size.toInt())

    @Get("/{id}")
    fun getById(@PathVariable("id") id: String): Event? = eventService.findById(id)

    @Put("/{id}")
    fun update(
        @PathVariable("id") id: String,
        eventRequest: EventRequest
    ): Event {
        val domain = eventRequest.toDomain()
        return eventService.update(id, domain)
    }

    @Put("/{eventId}/users/{userId}/accept")
    fun join(
        @PathVariable("eventId") eventId: String,
        @PathVariable("userId") userId: String,
    ): HttpResponse<Void> {
        eventService.join(eventId, userId)
        return HttpResponse.noContent()
    }

    @Put("/{eventId}/users/{userId}/disavow")
    fun remove(
        @PathVariable("eventId") eventId: String,
        @PathVariable("userId") userId: String,
    ): HttpResponse<Void> {
        eventService.remove(eventId, userId)
        return HttpResponse.noContent()
    }

    @Post("/{id}/notification/send")
    fun sendNotification(
        @PathVariable("id") eventId: String,
        notificationRequest: CreateNotificationRequest
    ): HttpResponse<Map<String, String>> {
        val notificationId =
            eventService.sendNotification(eventId, notificationRequest.title, notificationRequest.message)

        return HttpResponse.created(mapOf("id" to notificationId))
    }
}
