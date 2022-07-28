package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.resources.repository.entities.MessageEntity
import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.PageableRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PostgresMessageRepository : PageableRepository<MessageEntity, String> {

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    fun findByEventId(eventId: String, pageable: Pageable): List<MessageEntity>

    @Query("DELETE FROM messages WHERE event_id = :eventId")
    fun deleteByEvent(eventId: String)

    @Query("DELETE FROM messages WHERE user_id = :userId")
    fun deleteByUser(userId: String)
}
