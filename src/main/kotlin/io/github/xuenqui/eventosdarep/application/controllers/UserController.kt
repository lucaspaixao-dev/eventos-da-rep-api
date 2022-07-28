package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.UserRequest
import io.github.xuenqui.eventosdarep.application.controllers.requests.toDomain
import io.github.xuenqui.eventosdarep.application.controllers.requests.validations.validateRequest
import io.github.xuenqui.eventosdarep.domain.services.UserService
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.created
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/users")
class UserController(
    private val userService: UserService
) {

    @Post
    @Secured(SecurityRule.IS_ANONYMOUS)
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
//    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun findAll(
        @QueryValue(value = "page", defaultValue = "0") page: String,
        @QueryValue(value = "size", defaultValue = "20") size: String
    ) = userService.findAll(page.toInt(), size.toInt())

    @Get("/email/{email}")
//    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun findByEmail(
        @PathVariable("email") email: String
    ) = userService.findByEmail(email)

    @Put("/{id}")
//    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun update(
        @PathVariable("id") id: String,
        userRequest: UserRequest
    ): HttpResponse<Nothing> {
        logger.info("Request received to update user with id $id, $userRequest")

        userRequest.validateRequest()
        userService.update(id, userRequest.toDomain())
        return HttpResponse.noContent()
    }

    @Delete("/{id}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun delete(
        @PathVariable("id") id: String
    ): HttpResponse<Nothing> {
        logger.info("Request received to delete user with id $id")

        userService.deleteUser(id)
        return HttpResponse.noContent()
    }

    companion object : LoggableClass()
}
