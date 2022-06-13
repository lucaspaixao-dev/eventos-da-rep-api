package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.CreateNotificationRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.DeviceRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.UserRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.toDomain
import io.github.xuenqui.eventosdarep.domain.services.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.created
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue

@Controller("/users")
class UserController(
    private val userService: UserService
) {

    @Post
    fun create(userRequest: UserRequest): HttpResponse<Map<String, String>> {
        val domain = userRequest.toDomain()
        val uuid = userService.create(domain)

        return created(mapOf("id" to uuid))
    }

    @Get
    fun findAll(
        @QueryValue(value = "page", defaultValue = "0") page: String,
        @QueryValue(value = "size", defaultValue = "20") size: String,
    ) = userService.findAll(page.toInt(), size.toInt())

    @Put("/{id}")
    fun update(
        @PathVariable("id") id: String,
        userRequest: UserRequest
    ): HttpResponse<Nothing> {
        userService.update(id, userRequest.toDomain())
        return HttpResponse.noContent()
    }

    @Put("/{userId}/devices")
    fun updateDevice(
        @PathVariable("userId") userId: String,
        deviceRequest: DeviceRequest
    ): HttpResponse<Nothing> {
        val domain = deviceRequest.toDomain()
        userService.updateDevice(userId, domain)
        return HttpResponse.noContent()
    }

    @Post("/{userId}/notification/send")
    fun sendNotification(
        @PathVariable("userId") userId: String,
        notificationRequest: CreateNotificationRequest
    ): HttpResponse<Map<String, String>> {
        val notificationId =
            userService.sendNotification(userId, notificationRequest.title, notificationRequest.message)

        return created(mapOf("id" to notificationId))
    }
}
