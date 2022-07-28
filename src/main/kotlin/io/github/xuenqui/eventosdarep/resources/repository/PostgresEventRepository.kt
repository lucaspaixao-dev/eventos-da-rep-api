package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.resources.repository.entities.EventEntity
import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import io.micronaut.data.repository.PageableRepository
import java.util.Optional

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PostgresEventRepository : CrudRepository<EventEntity, String> {

//    @Join(value = "users", type = Join.Type.LEFT_FETCH)
//    override fun findAll(pageable: Pageable): Page<EventEntity>

    @Join(value = "users", type = Join.Type.LEFT_FETCH)
    override fun findAll(): List<EventEntity>

//    @Join(value = "users", type = Join.Type.LEFT_FETCH)
//    fun findByActive(active: Boolean, pageable: Pageable): List<EventEntity>

    @Join(value = "users", type = Join.Type.LEFT_FETCH)
    override fun findById(id: String): Optional<EventEntity>

    fun findByTitle(title: String): Optional<EventEntity>

    @Query("INSERT INTO event_entity_user_entity (event_entity_id, user_entity_id) VALUES (:eventId, :userId)")
    fun joinEvent(eventId: String, userId: String)

    @Query("DELETE FROM event_entity_user_entity WHERE event_entity_id = :eventId AND user_entity_id = :userId")
    fun exitEvent(eventId: String, userId: String)

    @Query("DELETE FROM event_entity_user_entity WHERE event_entity_id = :eventId")
    fun deleteEventOnEventUser(eventId: String)

    @Query("DELETE FROM event_entity_user_entity WHERE user_entity_id = :userId")
    fun deleteUserOnEventUser(userId: String)
}
