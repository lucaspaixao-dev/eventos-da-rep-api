package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.firebase.FirebaseMessagingService
import io.github.xuenqui.eventosdarep.repository.EventRepository
import jakarta.inject.Singleton

@Singleton
class EventService(
    private val eventRepository: EventRepository,
    private val userService: UserService,
    private val firebaseMessagingService: FirebaseMessagingService
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
            firebaseMessagingService.subscribeToTopic(user.device.token, eventId)
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
            firebaseMessagingService.unsubscribeFromTopic(user.device.token, eventId)
        }
    }

    // TODO: ADD TO RABBITMQ
    fun sendNotification(eventId: String, title: String, message: String): String {
        val event = eventRepository.findById(eventId) ?: throw IllegalArgumentException("Evento não encontrado")

        val newTitle = "${event.title}: $title"
        return firebaseMessagingService.sendNotificationToTopic(eventId, newTitle, message)
    }

    // TODO: ADD TO RABBITMQ
    private fun sendNotificationNewEvent(event: Event) {
        val title = "${event.title} disponível! 🤩"
        val message = "A REP tem um novo evento disponível! Abra o app e veja mais informações."

        val id = firebaseMessagingService.sendNotificationToTopic("users-topic", title, message)
        print(id)
    }

    // TODO: ADD TO RABBITMQ
    private fun sendNotificationToUsersOnEvent(user: User, event: Event) {
        val title = "${user.name} confirmou presença! 🎉"
        val message = "${user.name} confirmou presença no evento ${event.title}!"
        firebaseMessagingService.sendNotificationToTopic(event.id!!, title, message)
    }
}
