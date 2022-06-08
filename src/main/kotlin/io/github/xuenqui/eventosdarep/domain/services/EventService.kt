package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.repository.EventRepository
import jakarta.inject.Singleton

@Singleton
class EventService(
    private val eventRepository: EventRepository
) {

    fun create(event: Event) {
        eventRepository.save(event)
    }

    fun confirmation(event: Event, user: User) {
        event.confirm(user)
        eventRepository.update(event)
    }

    fun giveUp(event: Event, user: User) {
        event.giveUp(user)
        eventRepository.update(event)
    }

    fun findById(id: String): Event? = eventRepository.findById(id)

    fun findAll(): List<Event> = eventRepository.findAll()

    fun update(event: Event) {
        eventRepository.update(event)
    }
}
