package io.github.xuenqui.eventosdarep.application.controllers.requests

import io.github.xuenqui.eventosdarep.domain.Device
import io.github.xuenqui.eventosdarep.domain.User
import java.time.LocalDateTime

data class UserRequest(
    val name: String,
    val email: String,
    val authenticationId: String,
    val isAdmin: Boolean = false,
    val photo: String = "",
    val device: DeviceRequest
)

data class DeviceRequest(
    val token: String,
    val brand: String,
    val model: String
)

fun UserRequest.toDomain(id: String? = null, updatedAt: LocalDateTime? = null) = User(
    id = id,
    name = name,
    email = email,
    authenticationId = authenticationId,
    isAdmin = isAdmin,
    photo = photo,
    updatedAt = updatedAt,
    device = device.toDomain()
)

fun DeviceRequest.toDomain() = Device(
    token = token,
    brand = brand,
    model = model,
)
