package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.firebase.FirebaseMessagingService
import io.github.xuenqui.eventosdarep.repository.EventRepository
import io.github.xuenqui.eventosdarep.repository.UserRepository
import jakarta.inject.Singleton

@Singleton
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val firebaseMessagingService: FirebaseMessagingService
) {

    suspend fun create(event: Event): String {
        val id = eventRepository.save(event)
        sendNotificationNewEvent(event);
        return id
    }

    // TODO: ADD TO RABBITMQ
    private suspend fun sendNotificationNewEvent(event: Event) {
        val users = userRepository.findAll()
        val title = "${event.title} disponível! 🤩"
        val message = "A REP tem um novo evento disponível! Abra o app e veja mais informações."

        users.forEach { user ->
            val token = user.device?.token ?: return@forEach

            firebaseMessagingService.sendNotification(title, message, token)
        }
    }

    suspend fun findById(id: String): Event? = eventRepository.findById(id)

    suspend fun findAll(): List<Event> = eventRepository.findAll()

    suspend fun update(event: Event): Event {
        eventRepository.update(event)
        return event
    }
}
