package io.github.xuenqui.eventosdarep.resources.rabbitmq.listeners

import io.github.xuenqui.eventosdarep.resources.firebase.FirebaseMessagingService
import io.github.xuenqui.eventosdarep.resources.rabbitmq.Notification
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener

@RabbitListener
class NotificationListener(
    private val firebaseMessagingService: FirebaseMessagingService
) {

    @Queue("send-notification-user")
    fun receiveNotificationEvent(event: Notification) {
        firebaseMessagingService.sendNotificationToTopic(
            title = event.title,
            message = event.message,
            topic = event.topic,
            data = event.data
        )
    }
}
