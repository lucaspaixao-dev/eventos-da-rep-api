package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.CreateNotificationRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.DeviceRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.UserRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.toDomain
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.services.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.created
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import java.time.LocalDateTime

@Controller("/users")
class UserController(
    private val userService: UserService
) {

    @Post
    suspend fun create(userRequest: UserRequest): HttpResponse<Map<String, String>> {
        val domain = userRequest.toDomain()
        val uuid = userService.create(domain)

        return created(mapOf("id" to uuid))
    }

    @Get
    suspend fun findAll() = userService.findAll()

    @Put("/{id}")
    suspend fun update(
        @PathVariable("id") id: String,
        userRequest: UserRequest
    ): User {
        val domain = userRequest.toDomain(id = id, updatedAt = LocalDateTime.now())
        return userService.update(domain)
    }

    @Put("/{userId}/devices")
    suspend fun updateDevice(
        @PathVariable("userId") userId: String,
        deviceRequest: DeviceRequest
    ): HttpResponse<Nothing> {
        val domain = deviceRequest.toDomain()
        userService.updateDevice(userId, domain)
        return HttpResponse.noContent()
    }

    @Put("/{userId}/events/{eventId}/join")
    suspend fun join(
        @PathVariable("userId") userId: String,
        @PathVariable("eventId") eventId: String
    ): HttpResponse<Nothing> {
        userService.join(userId, eventId)
        return HttpResponse.noContent()
    }

    @Put("/{userId}/events/{eventId}/cancel")
    suspend fun cancel(
        @PathVariable("userId") userId: String,
        @PathVariable("eventId") eventId: String
    ): HttpResponse<Nothing> {
        userService.cancel(userId, eventId)
        return HttpResponse.noContent()
    }

    @Post("/{userId}/notification/send")
    suspend fun sendNotification(
        @PathVariable("userId") userId: String,
        notificationRequest: CreateNotificationRequest
    ): HttpResponse<Map<String, String>> {
        val notificationId =
            userService.sendNotification(userId, notificationRequest.title, notificationRequest.message)

        return created(mapOf("id" to notificationId))
    }
}
