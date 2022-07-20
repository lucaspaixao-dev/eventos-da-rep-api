package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Message
import io.github.xuenqui.eventosdarep.resources.repository.MessageRepository
import jakarta.inject.Singleton

@Singleton
class MessageService(
    private val messageRepository: MessageRepository,
    private val eventService: EventService,
    private val userService: UserService,
    private val notificationService: NotificationService
) {

    fun create(text: String, userId: String, eventId: String): String {
        val event = eventService.findById(eventId)
        val user = userService.findById(userId)

        val message = Message(
            text = text,
            type = "text",
            eventId = eventId,
            user = user
        )

        val id = messageRepository.create(message, event, user)
        val title = event.title
        val pushMessage = "${user.name}: $text"

        notificationService.sendNotificationToTopic(title, pushMessage, eventId)
        return id
    }

    fun findyByEventId(eventId: String) =
        messageRepository.findByEventId(eventId)
}
