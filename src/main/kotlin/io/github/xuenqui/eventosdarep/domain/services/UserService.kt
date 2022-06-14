package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Device
import io.github.xuenqui.eventosdarep.domain.User
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
        if (user.device == null) {
            throw IllegalArgumentException("Device não infornado")
        }

        val userId = userRepository.findByEmail(user.email)?.id ?: userRepository.create(user)

        notificationClient.sendUnsubscriptionOnTopicEvent(
            TopicMessage(
                topic = "users-topic",
                token = user.device.token
            )
        )

        return userId
    }

    fun findAll(page: Int, size: Int): List<User> = userRepository.findAll(page, size)

    fun findById(id: String): User? = userRepository.findById(id)

    fun update(userId: String, user: User) {
        val foundUser = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")

        if (user.device == null) {
            throw IllegalArgumentException("Device não infornado")
        }

        val newUser = foundUser.copy(
            name = user.name,
            email = user.email,
            device = user.device.copy(
                token = user.device.token,
                brand = user.device.brand,
                model = user.device.model,
                createdAt = user.device.createdAt
            ),
            authenticationId = user.authenticationId,
            isAdmin = user.isAdmin,
            photo = user.photo,
            createdAt = user.createdAt,
        )

        userRepository.update(newUser)
    }

    fun updateDevice(userId: String, device: Device) {
        val foundUser = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")

        val newUser = if (foundUser.device != null) {
            foundUser.copy(
                device = device.copy(
                    token = device.token,
                    brand = device.brand,
                    model = device.model,
                    createdAt = device.createdAt
                )
            )
        } else {
            foundUser.copy(
                device = Device(
                    id = UUID.randomUUID().toString(),
                    token = device.token,
                    brand = device.brand,
                    model = device.model,
                    createdAt = LocalDateTime.now()
                )
            )
        }

        userRepository.update(newUser)
    }

    fun sendNotification(userId: String, title: String, message: String) {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")

        if (user.device == null) {
            throw IllegalArgumentException("Usuário não possui um device registrado")
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
}
