package io.github.xuenqui.eventosdarep.resources.rabbitmq.clients

import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageUser
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient("eventos-da-rep-exchange")
interface NotificationClient {

    @Binding("send-notification-user")
    fun sendNotificationToUser(data: NotificationMessageUser)
}
