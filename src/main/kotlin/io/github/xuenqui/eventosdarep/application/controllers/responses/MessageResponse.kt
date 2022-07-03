package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.domain.Message
import io.github.xuenqui.eventosdarep.domain.User

data class MessageResponse(
    val author: AuthorResponse,
    val id: String,
    val status: String = "seen",
    val text: String,
    val type: String,
    val createdAt: Long,
) {
    constructor(message: Message) : this(
        createdAt = message.createdAt!!,
        id = message.id!!,
        text = message.text,
        type = message.type,
        author = AuthorResponse(message.user)
    )
}

data class AuthorResponse(
    val id: String,
    val imageUrl: String,
    val firstName: String,
    val lastName: String
) {
    constructor(user: User) : this(
        id = user.id!!,
        imageUrl = user.photo,
        firstName = user.name,
        lastName = ""
    )
}
