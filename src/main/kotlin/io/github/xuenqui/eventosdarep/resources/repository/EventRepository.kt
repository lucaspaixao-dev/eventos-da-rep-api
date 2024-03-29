package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.exceptions.RepositoryException
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.github.xuenqui.eventosdarep.resources.repository.entities.EventEntity
import io.micronaut.data.annotation.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
@SuppressWarnings("TooGenericExceptionCaught", "TooManyFunctions")
open class EventRepository(
    private val postgresEventRepository: PostgresEventRepository
) {

    fun findAll(): List<Event> =
        try {
            logger.info("finding all events")

            postgresEventRepository.findAll().map { it.toDomain() }
                .sortedBy { it.date }
                .toList()
        } catch (e: Exception) {
            throw RepositoryException("error finding all events", e)
        }

    fun findAllWithoutPage(): List<Event> =
        try {
            logger.info("finding all events")

            postgresEventRepository.findAll().map { it.toDomain() }.toList()
        } catch (e: Exception) {
            throw RepositoryException("error finding all events", e)
        }

    fun findByTitle(title: String): Event? =
        try {
            logger.info("finding event by title: $title")

            postgresEventRepository.findByTitle(title).orElse(null)?.toDomain()
        } catch (e: Exception) {
            throw RepositoryException("error finding event by title: $title", e)
        }

    fun findByActive(): List<Event> =
        try {
            logger.info("finding all active events")

            postgresEventRepository.findAll().map { it.toDomain() }
                .sortedBy { it.date }
                .toList()
        } catch (e: Exception) {
            throw RepositoryException("error finding all active events", e)
        }

    fun findById(eventId: String): Event? =
        try {
            postgresEventRepository.findById(eventId).also {
                logger.info("finding event by id: $eventId")
            }.map { it.toDomain() }.orElse(null)
        } catch (e: Exception) {
            throw RepositoryException("error finding event by id: $eventId", e)
        }

    fun create(event: Event): String =
        try {
            val eventId = UUID.randomUUID().toString()
            val eventEntity = EventEntity(eventId, event, LocalDateTime.now(), emptyList())

            logger.info("creating a new event $event")

            postgresEventRepository.save(eventEntity)

            eventId
        } catch (e: Exception) {
            throw RepositoryException("error creating event", e)
        }

    fun update(eventId: String, event: Event) =
        try {
            val eventEntity = EventEntity(eventId, event, event.createdAt, LocalDateTime.now())

            logger.info("updating the event $event")

            postgresEventRepository.update(eventEntity).toDomain()
        } catch (e: Exception) {
            throw RepositoryException("error updating event", e)
        }

    fun joinEvent(eventId: String, userId: String) {
        try {
            logger.info("user $userId joining the event $eventId")

            postgresEventRepository.joinEvent(eventId, userId)
        } catch (e: Exception) {
            throw RepositoryException("error joining event", e)
        }
    }

    fun exitEvent(eventId: String, userId: String) {
        try {
            logger.info("user $userId exiting the event $eventId")

            postgresEventRepository.exitEvent(eventId, userId)
        } catch (e: Exception) {
            throw RepositoryException("error exiting event", e)
        }
    }

    fun deleteUsersOnEvent(eventId: String) {
        try {
            logger.info("removing users on event $eventId")

            postgresEventRepository.deleteEventOnEventUser(eventId)
        } catch (e: Exception) {
            throw RepositoryException("error: ", e)
        }
    }

    fun delete(eventId: String) {
        try {
            logger.info("deleting event $eventId")

            postgresEventRepository.deleteById(eventId)
        } catch (e: Exception) {
            throw RepositoryException("error: ", e)
        }
    }

    fun deleteUserOnEvent(userId: String) {
        try {
            logger.info("deleting user $userId on events")
            postgresEventRepository.deleteUserOnEventUser(userId)
        } catch (e: Exception) {
            throw RepositoryException("error: ", e)
        }
    }

    companion object : LoggableClass()
}

fun EventEntity.toDomain() = Event(
    id = id,
    title = title,
    latitude = latitude,
    longitude = longitude,
    description = description,
    city = city,
    address = address,
    photo = photo,
    date = date,
    begin = begin,
    end = end,
    active = active,
    isPayed = isPayed,
    amount = amount,
    users = users.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt
)
