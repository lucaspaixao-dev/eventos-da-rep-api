package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.resources.repository.entities.UserEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.PageableRepository
import java.util.Optional

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PostgresUserRepository : PageableRepository<UserEntity, String> {
    fun findByEmail(email: String): Optional<UserEntity>
}
