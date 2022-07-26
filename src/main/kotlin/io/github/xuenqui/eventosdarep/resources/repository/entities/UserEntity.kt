package io.github.xuenqui.eventosdarep.resources.repository.entities

import io.github.xuenqui.eventosdarep.domain.User
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Index
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@MappedEntity("users")
data class UserEntity(
    @field:Id val id: String? = null,
    @field:NotNull val name: String,
    @field:NotNull @field:Index(name = "user_email_index", unique = true, columns = ["email"])
    val email: String,
    @field:NotNull val isAdmin: Boolean,
    @field:NotNull val photo: String,
    @field:NotNull @field:Index(
        name = "user_created_at_index",
        columns = ["created_at"]
    )
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
) {

    constructor(id: String, user: User, createdAt: LocalDateTime) : this(
        id = id,
        name = user.name,
        email = user.email,
        isAdmin = user.isAdmin,
        photo = user.photo,
        createdAt = createdAt,
        updatedAt = null
    )

    constructor(
        id: String,
        user: User,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime
    ) : this(
        id = id,
        name = user.name,
        email = user.email,
        isAdmin = user.isAdmin,
        photo = user.photo,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
