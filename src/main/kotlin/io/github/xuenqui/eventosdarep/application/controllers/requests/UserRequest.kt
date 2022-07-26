package io.github.xuenqui.eventosdarep.application.controllers.requests

import io.github.xuenqui.eventosdarep.domain.User

data class UserRequest(
    val name: String,
    val email: String,
    val isAdmin: Boolean = false,
    val photo: String
)

fun UserRequest.toDomain() = User(
    name = name,
    email = email,
    isAdmin = isAdmin,
    photo = photo
)
