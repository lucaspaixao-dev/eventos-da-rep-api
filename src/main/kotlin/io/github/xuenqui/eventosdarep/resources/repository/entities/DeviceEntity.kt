package io.github.xuenqui.eventosdarep.resources.repository.entities

import io.github.xuenqui.eventosdarep.domain.Device
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@MappedEntity("devices")
data class DeviceEntity(
    @field:Id val id: String? = null,
    @field:NotNull val token: String,
    @field:NotNull val brand: String,
    @field:NotNull val model: String,
    @field:NotNull val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
) {

    constructor(id: String, device: Device, createdAt: LocalDateTime) : this(
        id = id,
        token = device.token,
        brand = device.brand,
        model = device.model,
        createdAt = createdAt,
        updatedAt = null
    )

    constructor(id: String, device: Device, createdAt: LocalDateTime, updatedAt: LocalDateTime) : this(
        id = id,
        token = device.token,
        brand = device.brand,
        model = device.model,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
