package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class Event(
    var id: String? = UUID.randomUUID().toString(),
    var title: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var city: String = "",
    var address: String = "",
    var description: String = "",
    var photo: String = "",
    var date: LocalDateTime = LocalDateTime.now(),
    var begin: LocalTime = LocalTime.now(),
    var end: LocalTime = LocalTime.now(),
    var active: Boolean = true,
    var users: MutableList<String> = mutableListOf(),
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null
) {

    fun confirm(userId: String) {
        if (!users.contains(userId)) {
            users.add(userId)
        }
    }

    fun giveUp(userId: String) {
        if (users.contains(userId)) {
            users.remove(userId)
        }
    }
}
