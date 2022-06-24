package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageUser
import io.github.xuenqui.eventosdarep.resources.rabbitmq.clients.NotificationClient
import jakarta.inject.Singleton

@Singleton
class NotificationService(
    private val notificationClient: NotificationClient,
) {

    fun sendNotificationToTokens(title: String, message: String, tokens: List<String>) {
        if (tokens.isNotEmpty()) {
            notificationClient.sendNotificationToUser(
                data = NotificationMessageUser(
                    tokens = tokens,
                    title = title,
                    message = message
                )
            )
        }
    }
}
