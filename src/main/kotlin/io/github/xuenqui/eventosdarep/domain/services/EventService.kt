package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.repository.EventRepository
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class EventService(
    private val eventRepository: EventRepository
) {

    suspend fun create(event: Event): String {
        val id = eventRepository.save(event)
        return id
    }

    suspend fun findById(id: String): Event? = eventRepository.findById(id)

    suspend fun findAll(): List<Event> = eventRepository.findAll()

    suspend fun update(event: Event): Event {
        eventRepository.update(event)
        return event
    }
}
