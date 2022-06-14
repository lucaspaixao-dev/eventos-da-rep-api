package io.github.xuenqui.eventosdarep.resources.rabbitmq.clients

import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageTopic
import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageUser
import io.github.xuenqui.eventosdarep.resources.rabbitmq.TopicMessage
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient("eventos-da-rep-exchange")
interface NotificationClient {

    @Binding("send-notification-topic")
    fun sendNotificationEventToken(data: NotificationMessageTopic)

    @Binding("send-notification-user")
    fun sendNotificationEventUser(data: NotificationMessageUser)

    @Binding("subscription-on-topic")
    fun sendSubscriptionOnTopicEvent(data: TopicMessage)

    @Binding("unsubscription-on-topic")
    fun sendUnsubscriptionOnTopicEvent(data: TopicMessage)
}
