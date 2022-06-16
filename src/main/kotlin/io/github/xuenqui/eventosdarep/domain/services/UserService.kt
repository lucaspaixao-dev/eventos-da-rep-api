package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Device
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.domain.exceptions.ValidationException
import io.github.xuenqui.eventosdarep.resources.rabbitmq.NotificationMessageUser
import io.github.xuenqui.eventosdarep.resources.rabbitmq.TopicMessage
import io.github.xuenqui.eventosdarep.resources.rabbitmq.clients.NotificationClient
import io.github.xuenqui.eventosdarep.resources.repository.UserRepository
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.util.UUID

@Singleton
class UserService(
    private val userRepository: UserRepository,
    private val notificationClient: NotificationClient
) {

    fun create(user: User): String {
        validateDevice(user)

        val userId = userRepository.findByEmail(user.email)?.id ?: userRepository.create(user)

        notificationClient.sendSubscriptionOnTopicEvent(
            TopicMessage(
                topic = "users-topic",
                token = user.device!!.token
            )
        )

        return userId
    }

    fun findAll(page: Int, size: Int): List<User> = userRepository.findAll(page, size)

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

        notificationClient.sendNotificationEventUser(
            NotificationMessageUser(
                token = token,
                title = title,
                message = message
            )
        )
    }

    private fun getUserOrThrowAnException(userId: String) =
        userRepository.findById(userId) ?: throw ResourceNotFoundException("User not found")

    private fun validateDevice(user: User) {
        if (user.device == null) {
            throw ValidationException("Device is required")
        }
    }
}
