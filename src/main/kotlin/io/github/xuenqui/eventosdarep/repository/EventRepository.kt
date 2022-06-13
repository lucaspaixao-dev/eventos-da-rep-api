package io.github.xuenqui.eventosdarep.repository

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.repository.entities.EventEntity
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

        return postgresEventRepository.findAll(pageable).map { it.toDomain() }.toList()
    }

    fun findByActive(isActive: Boolean, page: Int, size: Int): List<Event> {
        val sort = Sort.UNSORTED.order(Sort.Order.asc("createdAt"))
        val pageable = Pageable.from(page, size, sort)

        return postgresEventRepository.findByActive(isActive, pageable).map { it.toDomain() }.toList()
    }

    fun findById(eventId: String): Event? =
        postgresEventRepository.findById(eventId).map { it.toDomain() }.orElse(null)

    fun create(event: Event): String {
        val eventId = UUID.randomUUID().toString()
        val eventEntity = EventEntity(eventId, event, LocalDateTime.now(), emptyList())

        postgresEventRepository.save(eventEntity)

        return eventId
    }

    fun update(eventId: String, event: Event) {
        val eventEntity = EventEntity(eventId, event, event.createdAt, LocalDateTime.now())

        postgresEventRepository.update(eventEntity)
    }

    fun joinEvent(eventId: String, userId: String) {
        postgresEventRepository.joinEvent(eventId, userId)
    }

    fun exitEvent(eventId: String, userId: String) {
        postgresEventRepository.exitEvent(eventId, userId)
    }
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
