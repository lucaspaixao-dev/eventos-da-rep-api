package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.resources.repository.entities.UserEntity
import io.micronaut.data.annotation.Join
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.PageableRepository
import java.util.Optional

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PostgresUserRepository : PageableRepository<UserEntity, String> {

    @Join(value = "device", type = Join.Type.LEFT_FETCH)
    override fun findAll(pageable: Pageable): Page<UserEntity>

    @Join(value = "device", type = Join.Type.LEFT_FETCH)
    override fun findById(id: String): Optional<UserEntity>

    @Join(value = "device", type = Join.Type.LEFT_FETCH)
    fun findByEmail(email: String): Optional<UserEntity>
}
