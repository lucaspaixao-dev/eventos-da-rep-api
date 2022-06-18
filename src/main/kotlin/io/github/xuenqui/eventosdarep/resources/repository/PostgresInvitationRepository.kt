package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.resources.repository.entities.InvitationEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.util.Optional

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PostgresInvitationRepository : CrudRepository<InvitationEntity, String> {

    fun findByEmail(email: String): Optional<InvitationEntity>

    fun deleteByEmail(email: String)
}
