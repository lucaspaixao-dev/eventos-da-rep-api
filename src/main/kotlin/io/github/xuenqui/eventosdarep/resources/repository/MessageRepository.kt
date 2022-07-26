package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.Message
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.exceptions.RepositoryException
import io.github.xuenqui.eventosdarep.resources.repository.entities.EventEntity
import io.github.xuenqui.eventosdarep.resources.repository.entities.MessageEntity
import io.github.xuenqui.eventosdarep.resources.repository.entities.UserEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

@Repository
@SuppressWarnings("TooGenericExceptionCaught")
open class MessageRepository(
    private val postgresMessageRepository: PostgresMessageRepository
) {

    fun create(message: Message, event: Event, user: User): String =
        try {
            val userEntity = UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                isAdmin = user.isAdmin,
                photo = user.photo,
                createdAt = user.createdAt!!,
                updatedAt = user.updatedAt
            )

            val eventEntity = EventEntity(
                id = event.id,
                title = event.title,
                latitude = event.latitude,
                longitude = event.longitude,
                city = event.city,
                address = event.address,
                description = event.description,
                photo = event.photo,
                date = event.date,
                begin = event.begin,
                end = event.end,
                active = event.active,
                createdAt = event.createdAt,
                updatedAt = event.updatedAt
            )

            val messageEntity = MessageEntity(
                id = UUID.randomUUID().toString(),
                text = message.text,
                type = message.type,
                user = userEntity,
                event = eventEntity,
                createdAt = LocalDateTime.now()
            )

            postgresMessageRepository.save(messageEntity)
            messageEntity.id!!
        } catch (e: Exception) {
            throw RepositoryException("error to create the message", e)
        }

    fun findByEventId(eventId: String): List<Message> =
        try {
            val sort = Sort.UNSORTED.order(Sort.Order.desc("createdAt"))
            val pageable = Pageable.from(sort)

            postgresMessageRepository.findByEventId(eventId, pageable).map { it.toDomain(eventId) }.toList()
        } catch (e: Exception) {
            throw RepositoryException("error finding messages by event id", e)
        }

    fun deleteByEvent(eventId: String) {
        try {
            postgresMessageRepository.deleteByEvent(eventId)
        } catch (e: Exception) {
            throw RepositoryException("error to delete messages by eventId $eventId", e)
        }
    }
}

fun MessageEntity.toDomain(eventId: String) = Message(
    id = this.id,
    text = this.text,
    type = this.type,
    user = this.user!!.toDomain(),
    eventId = eventId,
    createdAt = Timestamp.valueOf(this.createdAt).time
)
