package io.github.xuenqui.eventosdarep.application.controllers.requests

import io.github.xuenqui.eventosdarep.domain.User
import java.time.LocalDateTime

data class UserRequest(
    val name: String,
    val email: String,
    val authenticationId: String,
    val isAdmin: Boolean = false,
)

fun UserRequest.toDomain(id: String? = null, updatedAt: LocalDateTime? = null) = User(
    id = id,
    name = name,
    email = email,
    authenticationId = authenticationId,
    isAdmin = isAdmin,
    updatedAt = updatedAt
)
