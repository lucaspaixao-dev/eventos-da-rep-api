package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.domain.exceptions.UserNotInvitedException
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import io.github.xuenqui.eventosdarep.resources.repository.MessageRepository
import io.github.xuenqui.eventosdarep.resources.repository.UserRepository
import jakarta.inject.Singleton

@Singleton
class UserService(
    private val userRepository: UserRepository,
    private val invitationService: InvitationService,
    private val messageRepository: MessageRepository,
    private val eventRepository: EventRepository
) {

    fun create(user: User): String {
        return userRepository.findByEmail(user.email)?.id ?: validateInviteAndCreateUser(user)
    }

    fun findAll(page: Int, size: Int): List<User> = userRepository.findAll(page, size)

    fun findById(id: String): User = userRepository.findById(id) ?: throw ResourceNotFoundException("User not found")

    fun findByEmail(email: String): User = userRepository.findByEmail(email)
        ?: throw ResourceNotFoundException("User not found")

    fun update(userId: String, user: User) {
        val foundUser = getUserOrThrowAnException(userId)

        val newUser = foundUser.copy(
            name = user.name,
            email = user.email,
            isAdmin = user.isAdmin,
            photo = user.photo,
            createdAt = user.createdAt
        )

        userRepository.update(newUser)
    }

    fun deleteUser(userId: String) {
        val user = findById(userId)

        messageRepository.deleteByUser(user.id!!)
        eventRepository.deleteUserOnEvent(user.id)
        invitationService.delete(user.email)
        userRepository.delete(user.id)
    }

    private fun getUserOrThrowAnException(userId: String) =
        userRepository.findById(userId) ?: throw ResourceNotFoundException("User not found")

    private fun validateInviteAndCreateUser(user: User): String {
        val invitation =
            invitationService.findByEmail(user.email) ?: throw UserNotInvitedException("the user is not invited")

        val userId = userRepository.create(user)

        invitationService.delete(invitation.email)
        return userId
    }
}
