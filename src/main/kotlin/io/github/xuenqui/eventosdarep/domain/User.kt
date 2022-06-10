package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime
import java.util.UUID

data class User(
    var id: String? = UUID.randomUUID().toString(),
    var name: String = "",
    var email: String = "",
    var authenticationId: String = "",
    var isAdmin: Boolean = false,
    var photo: String = "",
    var events: MutableList<String> = mutableListOf(),
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null,
    var device: Device? = null
) {

    fun confirm(eventId: String) {
        if (!events.contains(eventId)) {
            events.add(eventId)
        }
    }

    fun remove(eventId: String) {
        if (events.contains(eventId)) {
            events.remove(eventId)
        }
    }
}
