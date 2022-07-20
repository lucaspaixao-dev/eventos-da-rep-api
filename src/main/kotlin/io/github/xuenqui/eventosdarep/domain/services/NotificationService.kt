package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.resources.rabbitmq.Notification
import io.github.xuenqui.eventosdarep.resources.rabbitmq.clients.NotificationClient
import jakarta.inject.Singleton

@Singleton
class NotificationService(
    private val notificationClient: NotificationClient
) {

    fun sendNotificationToTopic(title: String, message: String, topic: String) {
        notificationClient.sendNotificationToTopic(
            data = Notification(
                topic = topic,
                title = title,
                message = message
            )
        )
    }
}
