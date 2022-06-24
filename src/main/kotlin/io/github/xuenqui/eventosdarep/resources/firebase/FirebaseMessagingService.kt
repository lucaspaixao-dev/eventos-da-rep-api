package io.github.xuenqui.eventosdarep.resources.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import jakarta.inject.Singleton

@Singleton
class FirebaseMessagingService(
    private val firebaseMessaging: FirebaseMessaging
) {

    fun sendNotificationToToken(title: String, body: String, tokens: List<String>) {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        tokens.forEach { token ->
            val message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build()

            logger.info("Sending notification to user $token with the title $title and body $body")

            firebaseMessaging.send(message).also {
                logger.info("Notification sent: $it")
            }
        }
    }

    companion object : LoggableClass()
}
