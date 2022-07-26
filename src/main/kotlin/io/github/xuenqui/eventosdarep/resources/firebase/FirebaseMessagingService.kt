package io.github.xuenqui.eventosdarep.resources.firebase

import com.google.firebase.messaging.*
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import jakarta.inject.Singleton

@Singleton
class FirebaseMessagingService(
    private val firebaseMessaging: FirebaseMessaging
) {
    fun sendNotificationToTopic(
        title: String,
        message: String,
        topic: String,
        data: Map<String, String>
    ) {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(message)
            .build()

        val androidConfig = AndroidConfig.builder()
            .setNotification(
                AndroidNotification.builder()
                    .setSound("default")
                    .build()
            )
            .setPriority(AndroidConfig.Priority.HIGH)
            .build()

        val apnConfig = ApnsConfig.builder()
            .setAps(
                Aps.builder()
                    .setBadge(42)
                    .setSound("default")
                    .build()
            )
            .putHeader("apns-priority", "10")
            .build()

        val push = Message.builder()
            .putAllData(data)
            .setTopic(topic)
            .setNotification(notification)
            .setAndroidConfig(androidConfig)
            .setApnsConfig(apnConfig)
            .build()

        logger.info("Sending notification to topic $topic with the title $title and body $push")
        val response = firebaseMessaging.send(push)
        logger.info("Notification sent: $response")
    }

    companion object : LoggableClass()
}
