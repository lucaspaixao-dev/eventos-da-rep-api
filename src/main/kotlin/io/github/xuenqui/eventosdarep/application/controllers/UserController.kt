package io.github.xuenqui.eventosdarep.application.controllers

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
import io.micronaut.http.annotation.QueryValue
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

    @Put("/{id}")
    suspend fun update(
        @PathVariable("id") id: String,
        userRequest: UserRequest
    ): User {
        val domain = userRequest.toDomain(id = id, updatedAt = LocalDateTime.now())
        return userService.update(domain)
    }

    @Get
    suspend fun findByEmail(
        @QueryValue email: String
    ): User? {
        return userService.findByEmail(email)
    }
}
