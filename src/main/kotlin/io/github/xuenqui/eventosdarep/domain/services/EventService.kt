package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.firebase.FirebaseMessagingService
import io.github.xuenqui.eventosdarep.repository.EventRepository
import jakarta.inject.Singleton

@Singleton
class EventService(
    private val eventRepository: EventRepository,
    private val firebaseMessagingService: FirebaseMessagingService
) {

    suspend fun create(event: Event): String {
        val id = eventRepository.save(event)
        sendNotificationNewEvent(event)
        return id
    }

    suspend fun findById(id: String): Event? = eventRepository.findById(id)

    suspend fun findAll(): List<Event> = eventRepository.findAll()

    suspend fun findActiveEvents(): List<Event> = eventRepository.findActiveEvents()

    suspend fun update(eventId: String, event: Event): Event {
        val existsEvent = eventRepository.findById(eventId) ?: throw IllegalArgumentException("Evento não encontrado")

        val newEvent = event.copy(
            id = eventId,
            users = existsEvent.users,
            createdAt = existsEvent.updatedAt,
            updatedAt = existsEvent.updatedAt
        )

        eventRepository.update(newEvent)
        return event
    }

    suspend fun sendNotification(eventId: String, title: String, message: String): String {
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
}
