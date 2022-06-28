package io.github.xuenqui.eventosdarep.domain

data class Message(
    val id: String? = null,
    val text: String,
    val type: String = "text",
    val eventId: String,
    val user: User,
    val createdAt: Long? = null
)
