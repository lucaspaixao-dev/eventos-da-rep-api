package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceAlreadyExistsException
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.domain.exceptions.ValidationException
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import jakarta.inject.Singleton
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
                val time = LocalDateTime.now()
                    .withHour(it.end.hour)
                    .withMinute(it.end.minute)
                    .withSecond(it.end.second)
                time >= it.date
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
        getDeviceOrThrowAnException(user)

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
        getDeviceOrThrowAnException(user)

        event.users.find {
            it.id == user.id
        }?.let {
            eventRepository.exitEvent(eventId, it.id!!)
        }
    }

    fun sendNotification(eventId: String, title: String, message: String) {
        val event = getEventOrThrowAnException(eventId)
        val newTitle = "${event.title}: $title"
        val tokens = getUsersToken(event)

        notificationService.sendNotificationToTokens(newTitle, message, tokens)
    }

    private fun sendNotificationNewEvent(event: Event) {
        val tokens = mutableListOf<String>()
        val title = "${event.title} disponível! 🤩"
        val message = "A REP tem um novo evento disponível! Abra o app e veja mais informações."

        userService.findAllWithoutPage().forEach { user ->
            if (user.device != null) {
                tokens.add(user.device.token)
            }
        }

        notificationService.sendNotificationToTokens(title, message, tokens)
    }

    private fun sendNotificationToUsersOnEvent(user: User, event: Event) {
        val tokens = getUsersToken(event)
        val title = "${user.name} confirmou presença! 🎉"
        val message = "${user.name} confirmou presença no evento ${event.title}!"

        notificationService.sendNotificationToTokens(title, message, tokens)
    }

    private fun getUsersToken(event: Event): MutableList<String> {
        val tokens = mutableListOf<String>()
        event.users.forEach {
            userService.findById(it.id!!).device?.run {
                tokens.add(this.token)
            }
        }
        return tokens
    }

    private fun getUserOrThrowAnException(userId: String) = userService.findById(userId)

    private fun getEventOrThrowAnException(eventId: String) =
        eventRepository.findById(eventId) ?: throw ResourceNotFoundException("Event not found")

    private fun getDeviceOrThrowAnException(user: User) {
        if (user.device == null) {
            throw ValidationException("User must have a device")
        }
    }
}
