package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime
import java.util.UUID

data class User(
    var id: String? = UUID.randomUUID().toString(),
    var name: String = "",
    var email: String = "",
    var authenticationId: String = "",
    var isAdmin: Boolean = false,
    var events: MutableList<Event> = mutableListOf(),
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null
) {

    fun confirm(event: Event) {
        if (!events.contains(event)) {
            events.add(event)
        }
    }

    fun remove(event: Event) {
        if (events.contains(event)) {
            events.remove(event)
        }
    }
}
