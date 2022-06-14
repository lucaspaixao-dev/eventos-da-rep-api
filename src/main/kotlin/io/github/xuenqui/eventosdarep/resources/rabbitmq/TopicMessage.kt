package io.github.xuenqui.eventosdarep.resources.rabbitmq

data class TopicMessage(
    val topic: String,
    val token: String
)
