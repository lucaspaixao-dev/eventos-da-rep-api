package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class Event(
    val id: String? = UUID.randomUUID().toString(),
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val address: String,
    val description: String,
    val photo: String,
    val date: LocalDateTime,
    val begin: LocalTime,
    val end: LocalTime,
    val active: Boolean = true,
    val users: MutableList<User> = mutableListOf(),
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
) {

    fun confirm(user: User) {
        if (!users.contains(user)) {
            users.add(user)
        }
    }

    fun giveUp(user: User) {
        if (users.contains(user)) {
            users.remove(user)
        }
    }
}
