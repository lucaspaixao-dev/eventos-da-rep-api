package io.github.xuenqui.eventosdarep.resources.repository.entities

import io.github.xuenqui.eventosdarep.domain.Event
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Index
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import java.time.LocalDateTime
import java.time.LocalTime
import javax.validation.constraints.NotNull

@MappedEntity("events")
data class EventEntity(
    @field:Id val id: String? = null,
    @field:NotNull @field:Index(name = "event_title_index", unique = true, columns = ["title"]) val title: String,
    @field:NotNull val latitude: Double,
    @field:NotNull val longitude: Double,
    @field:NotNull val city: String,
    @field:NotNull val address: String,
    @field:NotNull val description: String,
    @field:NotNull val photo: String,
    @field:NotNull val date: LocalDateTime,
    @field:NotNull val begin: LocalTime,
    @field:NotNull val end: LocalTime,
    @field:NotNull @field:Index(name = "event_active_index", columns = ["active"]) val active: Boolean,
    @field:NotNull @field:Index(
        name = "event_created_at_index",
        columns = ["created_at"]
    ) val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,

    @field:Relation(
        Relation.Kind.MANY_TO_MANY,
        cascade = [Relation.Cascade.NONE],
    ) val users: List<UserEntity> = emptyList()
) {

    constructor(id: String, event: Event, createdAt: LocalDateTime, users: List<UserEntity>? = null) : this(
        id = id,
        title = event.title,
        latitude = event.latitude,
        longitude = event.longitude,
        city = event.city,
        address = event.address,
        description = event.description,
        photo = event.photo,
        date = event.date,
        begin = event.begin,
        end = event.end,
        active = event.active,
        createdAt = createdAt,
        updatedAt = null,
        users = users ?: emptyList()
    )

    constructor(
        id: String,
        event: Event,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime,
        users: List<UserEntity>? = null
    ) : this(
        id = id,
        title = event.title,
        latitude = event.latitude,
        longitude = event.longitude,
        city = event.city,
        address = event.address,
        description = event.description,
        photo = event.photo,
        date = event.date,
        begin = event.begin,
        end = event.end,
        active = event.active,
        createdAt = createdAt,
        updatedAt = updatedAt,
        users = users ?: emptyList()
    )
}
