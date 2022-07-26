package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.resources.rabbitmq.Notification
import io.github.xuenqui.eventosdarep.resources.rabbitmq.clients.NotificationClient
import jakarta.inject.Singleton

@Singleton
class NotificationService(
    private val notificationClient: NotificationClient
) {

    fun sendNotificationToTopic(
        title: String,
        message: String,
        topic: String,
        data: Map<String, String>
    ) {
        notificationClient.sendNotificationToTopic(
            data = Notification(
                topic = topic,
                title = title,
                message = message,
                data = data
            )
        )
    }

    fun createClickableNotification(eventId: String, notificationDestination: NotificationDestination) =
        mapOf(
            "click_action" to CLICK_ACTION,
            "event_id" to eventId,
            "screen" to notificationDestination.destination
        )

    fun createClickableNotification(event: Event, notificationDestination: NotificationDestination) =
        mapOf(
            "click_action" to CLICK_ACTION,
            "event_id" to event.id!!,
            "event_name" to event.title,
            "screen" to notificationDestination.destination
        )

    companion object {
        const val CLICK_ACTION = "FLUTTER_NOTIFICATION_CLICK"
    }
}

enum class NotificationDestination(val destination: String) {
    CHAT("event_chat"),
    EVENT_DETAILS("event_details")
}
