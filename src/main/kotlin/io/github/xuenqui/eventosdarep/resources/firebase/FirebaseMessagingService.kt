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

    fun subscribeToTopic(token: String, topic: String) {
        logger.info("Subscribing $token to topic $topic")

        val response = firebaseMessaging.subscribeToTopic(listOf(token), topic)
        logger.info("Subscription response: success ${response.successCount}, errors ${response.failureCount}")
    }

    fun unsubscribeFromTopic(token: String, topic: String) {
        logger.info("Unsubscribing $token to topic $topic")
        val response = firebaseMessaging.unsubscribeFromTopic(listOf(token), topic)
        logger.info("Unsubscribing response: success ${response.successCount}, errors ${response.failureCount}")
    }

    fun sendNotificationToTopic(topic: String, title: String, body: String): String {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message = Message.builder()
            .setNotification(notification)
            .setTopic(topic)
            .build()

        logger.info("Sending notification to topic $topic with the title $title and body $body")

        return firebaseMessaging.send(message).also {
            logger.info("Notification sent: $it")
        }
    }

    fun sendNotificationToToken(title: String, body: String, token: String): String {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message = Message.builder()
            .setToken(token)
            .setNotification(notification)
            .build()

        logger.info("Sending notification to user $token with the title $title and body $body")

        return firebaseMessaging.send(message).also {
            logger.info("Notification sent: $it")
        }
    }

    companion object : LoggableClass()
}
