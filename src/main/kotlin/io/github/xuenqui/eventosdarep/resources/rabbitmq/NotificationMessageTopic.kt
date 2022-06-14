package io.github.xuenqui.eventosdarep.resources.rabbitmq

data class NotificationMessageTopic(
    val topic: String,
    val title: String,
    val message: String
)
