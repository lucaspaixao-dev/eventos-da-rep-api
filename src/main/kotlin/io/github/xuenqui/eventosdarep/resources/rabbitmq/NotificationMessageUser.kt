package io.github.xuenqui.eventosdarep.resources.rabbitmq

data class NotificationMessageUser(
    val token: String,
    val title: String,
    val message: String
)
