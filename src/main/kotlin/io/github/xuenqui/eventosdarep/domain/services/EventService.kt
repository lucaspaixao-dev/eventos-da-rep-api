package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceAlreadyExistsException
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.LocalDateTime

@Singleton
@SuppressWarnings("TooManyFunctions")
class EventService(
    private val eventRepository: EventRepository,
    private val userService: UserService,
    private val notificationService: NotificationService
) {

    fun create(event: Event): String {
        eventRepository.findByTitle(event.title)
            ?.run { throw ResourceAlreadyExistsException("Event with title ${event.title} already exists") }

        return eventRepository.create(event).also {
            sendNotificationNewEvent(event)
        }
    }

    fun findById(id: String): Event = getEventOrThrowAnException(id)

    fun findAll(page: Int, size: Int): List<Event> = eventRepository.findAll(page, size)

    fun findActiveEvents(page: Int, size: Int): List<Event> =
        eventRepository.findByActive(true, page, size)
            .filter {
                val eventTime = it.date

                val currentTime = LocalDate.now()

                eventTime >= currentTime
            }.toList()

    fun update(eventId: String, event: Event): Event {
        val existsEvent = getEventOrThrowAnException(eventId)

        val newEvent = event.copy(
            id = existsEvent.id!!,
            users = existsEvent.users,
            createdAt = existsEvent.createdAt,
            updatedAt = LocalDateTime.now()
        )

        return eventRepository.update(eventId, newEvent)
    }

    fun join(eventId: String, userId: String) {
        val event = getEventOrThrowAnException(eventId)
        val user = getUserOrThrowAnException(userId)

        val isGoing = event.users.any {
            it.id == userId
        }

        if (!isGoing) {
            eventRepository.joinEvent(eventId, userId)
            sendNotificationToUsersOnEvent(user, event)
        }
    }

    fun remove(eventId: String, userId: String) {
        val event = getEventOrThrowAnException(eventId)
        val user = getUserOrThrowAnException(userId)

        event.users.find {
            it.id == user.id
        }?.let {
            eventRepository.exitEvent(eventId, it.id!!)
        }
    }

    fun sendNotification(eventId: String, title: String, message: String) {
        val event = getEventOrThrowAnException(eventId)
        val newTitle = "${event.title}: $title"
        val topic = event.id!!

        val notification = notificationService.createClickableNotification(eventId, NotificationDestination.EVENT_DETAILS)
        notificationService.sendNotificationToTopic(newTitle, message, topic, notification)
    }

    private fun sendNotificationNewEvent(event: Event) {
        val title = "${event.title} disponível! 🤩"
        val message = "A REP tem um novo evento disponível! Abra o app e veja mais informações."
        val topic = "users-topic"

        notificationService.sendNotificationToTopic(title, message, topic, emptyMap())
    }

    private fun sendNotificationToUsersOnEvent(user: User, event: Event) {
        val title = "${user.name} confirmou presença! 🎉"
        val message = "${user.name} confirmou presença no evento ${event.title}!"
        val topic = event.id!!

        val notification = notificationService.createClickableNotification(event.id, NotificationDestination.EVENT_DETAILS)
        notificationService.sendNotificationToTopic(title, message, topic, notification)
    }

    private fun getUserOrThrowAnException(userId: String) = userService.findById(userId)

    private fun getEventOrThrowAnException(eventId: String) =
        eventRepository.findById(eventId) ?: throw ResourceNotFoundException("Event not found")
}
