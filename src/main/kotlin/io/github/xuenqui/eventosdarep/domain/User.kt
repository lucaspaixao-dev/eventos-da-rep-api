package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime

data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val isAdmin: Boolean,
    val photo: String,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
    val device: Device? = null
)
