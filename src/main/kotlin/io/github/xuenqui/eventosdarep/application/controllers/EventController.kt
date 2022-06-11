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
import java.time.LocalDateTime

@Controller("/events")
class EventController(
    private val eventService: EventService
) {

    @Post
    suspend fun create(eventRequest: EventRequest): HttpResponse<Map<String, String>> {
        val domain = eventRequest.toDomain()
        val uuid = eventService.create(domain)

        return HttpResponse.created(mapOf("id" to uuid))
    }

    @Get
    suspend fun getAll(): List<Event> = eventService.findAll()

    @Get("/actives")
    suspend fun getActiveEvents(): List<Event> = eventService.findActiveEvents()

    @Get("/{id}")
    suspend fun getById(@PathVariable("id") id: String): Event? = eventService.findById(id)

    @Put("/{id}")
    suspend fun update(
        @PathVariable("id") id: String,
        eventRequest: EventRequest
    ): Event {
        val domain = eventRequest.toDomain()
        return eventService.update(id, domain)
    }

    @Post("/{id}/notification/send")
    suspend fun sendNotification(
        @PathVariable("id") eventId: String,
        notificationRequest: CreateNotificationRequest
    ): HttpResponse<Map<String, String>> {
        val notificationId =
            eventService.sendNotification(eventId, notificationRequest.title, notificationRequest.message)

        return HttpResponse.created(mapOf("id" to notificationId))
    }
}
