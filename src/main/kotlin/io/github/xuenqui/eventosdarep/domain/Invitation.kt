package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime

data class Invitation(
    val id: String? = null,
    val email: String,
    val createdAt: LocalDateTime? = LocalDateTime.now()
)
