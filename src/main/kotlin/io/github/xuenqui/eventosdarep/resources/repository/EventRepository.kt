package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.github.xuenqui.eventosdarep.resources.repository.entities.EventEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import java.time.LocalDateTime
import java.util.UUID

@Repository
open class EventRepository(
    private val postgresEventRepository: PostgresEventRepository
) {

    fun findAll(page: Int, size: Int): List<Event> {
        val sort = Sort.UNSORTED.order(Sort.Order.asc("createdAt"))
        val pageable = Pageable.from(page, size, sort)

        logger.info("finding all events")

        return postgresEventRepository.findAll(pageable).map { it.toDomain() }.toList()
    }

    fun findByActive(isActive: Boolean, page: Int, size: Int): List<Event> {
        val sort = Sort.UNSORTED.order(Sort.Order.asc("createdAt"))
        val pageable = Pageable.from(page, size, sort)

        logger.info("finding all active events")

        return postgresEventRepository.findByActive(isActive, pageable).map { it.toDomain() }.toList()
    }

    fun findById(eventId: String): Event? =
        postgresEventRepository.findById(eventId).also {
            logger.info("finding event by id: $eventId")
        }.map { it.toDomain() }.orElse(null)

    fun create(event: Event): String {
        val eventId = UUID.randomUUID().toString()
        val eventEntity = EventEntity(eventId, event, LocalDateTime.now(), emptyList())

        logger.info("creating a new event $event")

        postgresEventRepository.save(eventEntity)

        return eventId
    }

    fun update(eventId: String, event: Event) {
        val eventEntity = EventEntity(eventId, event, event.createdAt, LocalDateTime.now())

        logger.info("updating the event $event")

        postgresEventRepository.update(eventEntity)
    }

    fun joinEvent(eventId: String, userId: String) {
        logger.info("user $userId joining the event $eventId")

        postgresEventRepository.joinEvent(eventId, userId)
    }

    fun exitEvent(eventId: String, userId: String) {
        logger.info("user $userId exiting the event $eventId")

        postgresEventRepository.exitEvent(eventId, userId)
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
    users = users.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt
)
