package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.repository.UserRepository
import jakarta.inject.Singleton

@Singleton
class UserService(
    private val userRepository: UserRepository,
    private val eventService: EventService
) {

    suspend fun create(user: User): String =
        userRepository.findByEmail(user.email)?.id ?: userRepository.save(user)

    suspend fun join(userId: String, eventId: String) {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")
        val event = eventService.findById(eventId) ?: throw IllegalArgumentException("Event not found")

        event.confirm(userId)
        user.confirm(eventId)
        eventService.update(event)
        userRepository.update(user)
    }

    suspend fun cancel(userId: String, eventId: String) {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")
        val event = eventService.findById(eventId) ?: throw IllegalArgumentException("Event not found")

        event.giveUp(userId)
        user.remove(eventId)
        eventService.update(event)
        userRepository.update(user)
    }

    suspend fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    suspend fun findById(id: String): User? = userRepository.findById(id)

    suspend fun findAll(): List<User> = userRepository.findAll()

    suspend fun update(user: User): User {
        // TODO: ajustar createdAt
        userRepository.update(user)
        return user
    }
}
