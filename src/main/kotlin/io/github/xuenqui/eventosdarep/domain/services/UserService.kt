package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Device
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.domain.exceptions.UserNotInvitedException
import io.github.xuenqui.eventosdarep.domain.exceptions.ValidationException
import io.github.xuenqui.eventosdarep.resources.repository.UserRepository
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.util.UUID

@Singleton
@SuppressWarnings("TooManyFunctions")
class UserService(
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val invitationService: InvitationService
) {

    fun create(user: User): String {
        validateDevice(user)

        return userRepository.findByEmail(user.email)?.id ?: validateInviteAndCreateUser(user)
    }

    fun findAll(page: Int, size: Int): List<User> = userRepository.findAll(page, size)

    fun findAllWithoutPage(): List<User> = userRepository.findAllWithoutPage()

    fun findById(id: String): User = userRepository.findById(id) ?: throw ResourceNotFoundException("User not found")

    fun findByEmail(email: String): User = userRepository.findByEmail(email)
        ?: throw ResourceNotFoundException("User not found")

    fun update(userId: String, user: User) {
        val foundUser = getUserOrThrowAnException(userId)

        validateDevice(user)

        val newUser = foundUser.copy(
            name = user.name,
            email = user.email,
            device = user.device!!.copy(
                token = user.device.token,
                brand = user.device.brand,
                model = user.device.model,
                createdAt = user.device.createdAt
            ),
            isAdmin = user.isAdmin,
            photo = user.photo,
            createdAt = user.createdAt,
        )

        userRepository.update(newUser)
    }

    fun updateDevice(userId: String, device: Device) {
        val foundUser = getUserOrThrowAnException(userId)

        val newDevice = if (foundUser.device != null) {
            device.copy(
                id = foundUser.device.id,
                token = device.token,
                brand = device.brand,
                model = device.model,
                createdAt = device.createdAt,
                updatedAt = LocalDateTime.now()
            )
        } else {
            Device(
                id = UUID.randomUUID().toString(),
                token = device.token,
                brand = device.brand,
                model = device.model,
                createdAt = LocalDateTime.now()
            )
        }

        val newUser = foundUser.copy(
            device = newDevice
        )
        userRepository.update(newUser)
    }

    fun sendNotification(userId: String, title: String, message: String) {
        val user = getUserOrThrowAnException(userId)

        if (user.device == null) {
            throw ValidationException("User has no device")
        }

        val token = user.device.token
        notificationService.sendNotificationToTokens(title, message, listOf(token))
    }

    private fun getUserOrThrowAnException(userId: String) =
        userRepository.findById(userId) ?: throw ResourceNotFoundException("User not found")

    private fun validateDevice(user: User) {
        if (user.device == null) {
            throw ValidationException("Device is required")
        }
    }

    private fun validateInviteAndCreateUser(user: User): String {
        val invitation =
            invitationService.findByEmail(user.email) ?: throw UserNotInvitedException("the user is not invited")

        val userId = userRepository.create(user)

        invitationService.delete(invitation.email)
        return userId
    }
}
