package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.CreateNotificationRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.DeviceRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.UserRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.toDomain
import io.github.xuenqui.eventosdarep.application.controllers.requests.validations.validateRequest
import io.github.xuenqui.eventosdarep.domain.services.UserService
import io.github.xuenqui.eventosdarep.logging.LoggableClass
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
        logger.info("Request received to create a new user $userRequest")

        userRequest.validateRequest()
        val domain = userRequest.toDomain()
        val uuid = userService.create(domain)

        return created(mapOf("id" to uuid)).also {
            logger.info("User created with id $uuid")
        }
    }

    @Get
    fun findAll(
        @QueryValue(value = "page", defaultValue = "0") page: String,
        @QueryValue(value = "size", defaultValue = "20") size: String,
    ) = userService.findAll(page.toInt(), size.toInt())

    @Get("/email/{email}")
    fun findByEmail(
        @PathVariable("email") email: String
    ) = userService.findByEmail(email)

    @Put("/{id}")
    fun update(
        @PathVariable("id") id: String,
        userRequest: UserRequest
    ): HttpResponse<Nothing> {
        logger.info("Request received to update user with id $id, $userRequest")

        userRequest.validateRequest()
        userService.update(id, userRequest.toDomain())
        return HttpResponse.noContent()
    }

    @Put("/{userId}/devices")
    fun updateDevice(
        @PathVariable("userId") userId: String,
        deviceRequest: DeviceRequest
    ): HttpResponse<Nothing> {
        logger.info("Request received to update device for user with id $userId, $deviceRequest")
        val domain = deviceRequest.toDomain()
        userService.updateDevice(userId, domain)
        return HttpResponse.noContent()
    }

    @Post("/{userId}/notification/send")
    fun sendNotification(
        @PathVariable("userId") userId: String,
        notificationRequest: CreateNotificationRequest
    ): HttpResponse<Void> {
        logger.info("Request received to send notification for user with id $userId, $notificationRequest")
        userService.sendNotification(userId, notificationRequest.title, notificationRequest.message)

        return HttpResponse.noContent<Void?>().also {
            logger.info("Notification sent to user $userId")
        }
    }

    companion object : LoggableClass()
}
