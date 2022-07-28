package io.github.xuenqui.eventosdarep.resources.rabbitmq

data class Notification(
    val topic: String,
    val title: String,
    val message: String,
    val data: Map<String, String>
)
