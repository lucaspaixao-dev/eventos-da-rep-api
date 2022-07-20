package io.github.xuenqui.eventosdarep.application.controllers.requests

import io.github.xuenqui.eventosdarep.domain.Event
import java.time.LocalDateTime
import java.time.LocalTime

data class EventRequest(
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val address: String,
    val description: String,
    val photo: String,
    val eventDate: LocalDateTime,
    val begin: LocalTime,
    val end: LocalTime,
    val active: Boolean = true,
    val isPayed: Boolean? = false,
    val amount: Long? = null
)

fun EventRequest.toDomain() = Event(
    title = this.title,
    latitude = this.latitude,
    longitude = this.longitude,
    city = this.city,
    address = this.address,
    description = this.description,
    photo = this.photo,
    date = this.eventDate.toLocalDate(),
    begin = this.begin,
    end = this.end,
    active = this.active,
    isPayed = this.isPayed ?: false,
    amount = this.amount ?: 0
)
