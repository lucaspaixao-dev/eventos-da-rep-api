package io.github.xuenqui.eventosdarep.resources.firebase

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
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
                    .setBadge(DEFAULT_BADGE)
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

    companion object : LoggableClass() {
        const val DEFAULT_BADGE = 42
    }
}
