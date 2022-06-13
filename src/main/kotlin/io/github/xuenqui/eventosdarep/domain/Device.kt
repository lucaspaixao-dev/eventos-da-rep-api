package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime

data class Device(
    val id: String? = null,
    val token: String,
    val brand: String,
    val model: String,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
)
