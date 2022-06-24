package io.github.xuenqui.eventosdarep.resources.rabbitmq.listeners

import io.github.xuenqui.eventosdarep.resources.firebase.FirebaseMessagingService
import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageUser
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener

@RabbitListener
class NotificationListener(
    private val firebaseMessagingService: FirebaseMessagingService
) {

    @Queue("send-notification-user")
    fun receiveNotificationEvent(event: NotificationMessageUser) {
        firebaseMessagingService.sendNotificationToToken(
            title = event.title,
            body = event.message,
            tokens = event.tokens
        )
    }
}
