package io.github.xuenqui.eventosdarep.resources.rabbitmq.clients

import io.github.xuenqui.eventosdarep.resources.rabbitmq.Notification
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient("eventos-da-rep-exchange")
interface NotificationClient {

    @Binding("send-notification-user")
    fun sendNotificationToTopic(data: Notification)
}
