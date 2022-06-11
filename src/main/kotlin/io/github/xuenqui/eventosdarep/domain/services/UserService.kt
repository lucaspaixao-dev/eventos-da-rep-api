package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Device
import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.firebase.FirebaseMessagingService
import io.github.xuenqui.eventosdarep.repository.UserRepository
import jakarta.inject.Singleton

@Singleton
class UserService(
    private val userRepository: UserRepository,
    private val eventService: EventService,
    private val firebaseMessagingService: FirebaseMessagingService
) {

    suspend fun create(user: User): String {
        user.device ?: throw IllegalArgumentException("Device should not be null")

        val userId = userRepository.findByEmail(user.email)?.id ?: userRepository.save(user)

        firebaseMessagingService.subscribeToTopic(user.device!!.token, "users-topic")
        return userId
    }

    suspend fun join(userId: String, eventId: String) {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")
        val event = eventService.findById(eventId) ?: throw IllegalArgumentException("Event not found")

        event.confirm(userId)
        user.confirm(eventId)
        eventService.update(event.id!!, event)
        userRepository.update(user)

        sendNotificationToUsersOnEvent(user, event)
        firebaseMessagingService.subscribeToTopic(user.device!!.token, eventId)
    }

    suspend fun cancel(userId: String, eventId: String) {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")
        val event = eventService.findById(eventId) ?: throw IllegalArgumentException("Event not found")

        event.giveUp(userId)
        user.remove(eventId)
        eventService.update(event.id!!, event)
        userRepository.update(user)
        firebaseMessagingService.unsubscribeFromTopic(user.device!!.token, eventId)
    }

    suspend fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    suspend fun findById(id: String): User? = userRepository.findById(id)

    suspend fun findAll(): List<User> = userRepository.findAll()

    suspend fun update(user: User): User {
        // TODO: ajustar createdAt
        userRepository.update(user)
        return user
    }

    suspend fun updateDevice(userId: String, device: Device) {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")
        user.device = device
        userRepository.update(user)
    }

    suspend fun sendNotification(userId: String, title: String, message: String): String {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")
        val token = user.device?.token ?: throw IllegalArgumentException("User has no device")

        return firebaseMessagingService.sendNotificationToToken(title, message, token)
    }

    // TODO: ADD TO RABBITMQ
    private fun sendNotificationToUsersOnEvent(user: User, event: Event) {
        val title = "${user.name} confirmou presença! 🎉"
        val message = "${user.name} confirmou presença no evento ${event.title}!"
        firebaseMessagingService.sendNotificationToTopic(event.id!!, title, message)
    }
}
