package io.github.xuenqui.eventosdarep.resources.rabbitmq

data class NotificationMessageUser(
    val tokens: List<String>,
    val title: String,
    val message: String
)
