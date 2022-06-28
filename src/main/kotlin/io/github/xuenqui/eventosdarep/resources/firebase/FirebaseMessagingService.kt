package io.github.xuenqui.eventosdarep.resources.firebase

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import jakarta.inject.Singleton

@Singleton
class FirebaseMessagingService(
    private val firebaseMessaging: FirebaseMessaging
) {
    fun sendNotificationToTopic(title: String, messasge: String, topic: String) {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(messasge)
            .build()

        val androidConfig = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build()
        val apnConfig = ApnsConfig.builder().putHeader("apns-priority", "10").build()

        val message = Message.builder()
            .setTopic(topic)
            .setNotification(notification)
            .setAndroidConfig(androidConfig)
            .setApnsConfig(apnConfig)
            .build()

        logger.info("Sending notification to topic $topic with the title $title and body $message")
        val response = firebaseMessaging.send(message)
        logger.info("Notification sent: $response")
    }

    companion object : LoggableClass()
}
