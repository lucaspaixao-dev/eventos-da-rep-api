package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageTopic
import io.github.xuenqui.eventosdarep.resources.rabbitmq.TopicMessage
import io.github.xuenqui.eventosdarep.resources.rabbitmq.clients.NotificationClient
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import jakarta.inject.Singleton

@Singleton
class EventService(
    private val eventRepository: EventRepository,
    private val userService: UserService,
    private val notificationClient: NotificationClient
) {

    fun create(event: Event): String {
        val id = eventRepository.create(event)

        sendNotificationNewEvent(event)
        return id
    }

    fun findById(id: String): Event? = eventRepository.findById(id)

    fun findAll(page: Int, size: Int): List<Event> = eventRepository.findAll(page, size)

    fun findActiveEvents(page: Int, size: Int): List<Event> =
        eventRepository.findByActive(true, page, size)

    fun update(eventId: String, event: Event): Event {
        val existsEvent = eventRepository.findById(eventId) ?: throw IllegalArgumentException("Evento não encontrado")

        val newEvent = event.copy(
            id = eventId,
            users = existsEvent.users,
            createdAt = existsEvent.createdAt,
            updatedAt = existsEvent.updatedAt
        )

        eventRepository.update(eventId, newEvent)
        return event
    }

    fun join(eventId: String, userId: String) {
        val event = eventRepository.findById(eventId) ?: throw IllegalArgumentException("Evento não encontrado")
        val user = userService.findById(userId) ?: throw IllegalArgumentException("Usuário não encontrado")

        if (user.device == null) {
            throw IllegalArgumentException("Usuário não possui um device registrado")
        }

        var isGoing = false

        event.users.forEach {
            if (it.id == userId) {
                isGoing = true
            }
        }

        if (!isGoing) {
            eventRepository.joinEvent(eventId, userId)

            sendNotificationToUsersOnEvent(user, event)
            notificationClient.sendSubscriptionOnTopicEvent(
                TopicMessage(
                    topic = eventId,
                    token = user.device.token
                )
            )
        }
    }

    fun remove(eventId: String, userId: String) {
        val event = eventRepository.findById(eventId) ?: throw IllegalArgumentException("Evento não encontrado")
        val user = userService.findById(userId) ?: throw IllegalArgumentException("Usuário não encontrado")

        if (user.device == null) {
            throw IllegalArgumentException("Usuário não possui um device registrado")
        }

        event.users.find {
            it.id == user.id
        }?.let {
            eventRepository.exitEvent(eventId, it.id!!)

            notificationClient.sendUnsubscriptionOnTopicEvent(
                TopicMessage(
                    topic = eventId,
                    token = user.device.token
                )
            )
        }
    }

    fun sendNotification(eventId: String, title: String, message: String) {
        val event = eventRepository.findById(eventId) ?: throw IllegalArgumentException("Evento não encontrado")

        val newTitle = "${event.title}: $title"

        notificationClient.sendNotificationEventToken(
            NotificationMessageTopic(
                topic = eventId,
                message = message,
                title = newTitle
            )
        )
    }

    private fun sendNotificationNewEvent(event: Event) {
        val title = "${event.title} disponível! 🤩"
        val message = "A REP tem um novo evento disponível! Abra o app e veja mais informações."

        notificationClient.sendNotificationEventToken(
            NotificationMessageTopic(
                topic = "users-topic",
                message = message,
                title = title
            )
        )
    }

    private fun sendNotificationToUsersOnEvent(user: User, event: Event) {
        val title = "${user.name} confirmou presença! 🎉"
        val message = "${user.name} confirmou presença no evento ${event.title}!"

        notificationClient.sendNotificationEventToken(
            NotificationMessageTopic(
                topic = event.id!!,
                message = message,
                title = title
            )
        )
    }
}
