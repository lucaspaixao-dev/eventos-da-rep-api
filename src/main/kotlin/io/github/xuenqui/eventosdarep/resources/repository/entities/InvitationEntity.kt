package io.github.xuenqui.eventosdarep.resources.repository.entities

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Index
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@MappedEntity("invitations")
data class InvitationEntity(
    @field:Id
    val id: String? = null,
    @field:NotNull @field:Index(name = "invitation_email_index", unique = true, columns = ["email"])
    val email: String,
    @field:NotNull val createdAt: LocalDateTime = LocalDateTime.now()
)
