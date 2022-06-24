package mocks

import io.github.xuenqui.eventosdarep.domain.Device
import io.github.xuenqui.eventosdarep.domain.User
import java.time.LocalDateTime
import java.util.UUID

internal fun buildUserMock(id: String? = null) = User(
    id = id ?: UUID.randomUUID().toString(),
    name = "Test",
    email = "test@gmail.com",
    isAdmin = false,
    photo = "test",
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now(),
    device = Device(
        id = UUID.randomUUID().toString(),
        token = "addsa",
        brand = "Apple",
        model = "13",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
)
