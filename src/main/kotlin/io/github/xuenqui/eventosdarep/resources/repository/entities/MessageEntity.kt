package io.github.xuenqui.eventosdarep.resources.repository.entities

import io.micronaut.core.annotation.Nullable
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Index
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@MappedEntity("messages")
data class MessageEntity(
    @field:Id val id: String? = null,

    @field:NotNull val text: String,
    @field:NotNull val type: String,

    @Nullable @field:Relation(
        Relation.Kind.ONE_TO_ONE,
        cascade = [Relation.Cascade.PERSIST, Relation.Cascade.UPDATE]
    ) val user: UserEntity? = null,

    @Nullable @field:Relation(
        Relation.Kind.ONE_TO_ONE,
        cascade = [Relation.Cascade.PERSIST, Relation.Cascade.UPDATE]
    ) val event: EventEntity? = null,

    @field:NotNull @field:Index(
        name = "message_created_at_index",
        columns = ["created_at"]
    ) val createdAt: LocalDateTime = LocalDateTime.now()
)
