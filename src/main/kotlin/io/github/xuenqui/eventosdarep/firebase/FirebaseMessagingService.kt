package io.github.xuenqui.eventosdarep.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import jakarta.inject.Singleton

@Singleton
class FirebaseMessagingService(
    private val firebaseMessaging: FirebaseMessaging
) {

    fun subscribeToTopic(token: String, eventId: String) {
        val response = firebaseMessaging.subscribeToTopic(listOf(token), eventId)
        print(response.successCount)
    }

    fun unsubscribeFromTopic(token: String, eventId: String) {
        val response = firebaseMessaging.unsubscribeFromTopic(listOf(token), eventId)
    }

    fun sendNotificationToTopic(eventId: String, title: String, body: String): String {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message = Message.builder()
            .setNotification(notification)
            .setTopic(eventId)
            .build()

        return firebaseMessaging.send(message)
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

        print("Sending message: $message")

        return firebaseMessaging.send(message)
    }
}
