package io.github.xuenqui.eventosdarep.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import jakarta.inject.Singleton

@Singleton
class FirebaseMessagingService(
    private val firebaseMessaging: FirebaseMessaging
) {

    fun sendNotification(
        title: String,
        body: String,
        token: String
    ): String {
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
