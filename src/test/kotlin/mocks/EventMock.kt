package mocks

import io.github.xuenqui.eventosdarep.domain.Event
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

internal fun buildEventMock(id: String? = null) = Event(
    id = id?.let { id } ?: UUID.randomUUID().toString(),
    title = "Event test",
    latitude = -10.0,
    longitude = -15.0,
    city = "City test",
    address = "Address test",
    description = "Test",
    photo = "test photo",
    date = LocalDateTime.now(),
    begin = LocalTime.now(),
    end = LocalTime.now(),
    active = true,
    users = emptyList(),
    createdAt = LocalDateTime.now()
)
