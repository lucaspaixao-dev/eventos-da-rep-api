package io.github.xuenqui.eventosdarep.application.controllers.requests

data class MessageRequest(
    val eventId: String,
    val userId: String,
    val text: String
)
