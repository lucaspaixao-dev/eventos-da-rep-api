package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Event(
    val id: String? = null,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val address: String,
    val description: String,
    val photo: String,
    val date: LocalDate,
    val begin: LocalTime,
    val end: LocalTime,
    val active: Boolean,
    val isPayed: Boolean = false,
    val amount: Long? = null,
    val users: List<User> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
)
