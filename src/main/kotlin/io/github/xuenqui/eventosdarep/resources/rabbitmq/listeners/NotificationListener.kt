package io.github.xuenqui.eventosdarep.resources.rabbitmq.listeners

import io.github.xuenqui.eventosdarep.resources.firebase.FirebaseMessagingService
import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageTopic
import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageUser
import io.github.xuenqui.eventosdarep.resources.rabbitmq.TopicMessage
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener

@RabbitListener
class NotificationListener(
    private val firebaseMessagingService: FirebaseMessagingService
) {

    @Queue("send-notification-topic")
    fun receiveNotificationEvent(event: NotificationMessageTopic) {
        firebaseMessagingService.sendNotificationToTopic(
            title = event.title,
            body = event.message,
            topic = event.topic
        )
    }

    @Queue("send-notification-user")
    fun receiveNotificationEvent(event: NotificationMessageUser) {
        firebaseMessagingService.sendNotificationToToken(
            title = event.title,
            body = event.message,
            token = event.token
        )
    }

    @Queue("subscription-on-topic")
    fun receiveSubscriptionOnTopicEvent(event: TopicMessage) {
        firebaseMessagingService.subscribeToTopic(
            token = event.token,
            topic = event.topic
        )
    }

    @Queue("unsubscription-on-topic")
    fun receiveUnsubscriptionOnTopicEvent(event: TopicMessage) {
        firebaseMessagingService.unsubscribeFromTopic(
            token = event.token,
            topic = event.topic
        )
    }
}
