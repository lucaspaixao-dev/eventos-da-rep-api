package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.repository.UserRepository
import jakarta.inject.Singleton

@Singleton
class UserService(
    private val userRepository: UserRepository
) {

    suspend fun create(user: User): String {
        userRepository.save(user)
        return user.id!!
    }

    suspend fun confirmation(event: Event, user: User) {
        user.confirm(event)
        userRepository.update(user)
    }

    suspend fun giveUp(event: Event, user: User) {
        user.remove(event)
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
