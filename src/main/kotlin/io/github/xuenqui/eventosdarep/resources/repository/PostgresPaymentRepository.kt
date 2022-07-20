package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.resources.repository.entities.PaymentEntity
import io.micronaut.data.annotation.Join
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.util.Optional

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PostgresPaymentRepository : CrudRepository<PaymentEntity, String> {

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "event", type = Join.Type.LEFT_FETCH)
    fun findByIntentClientId(intentClientId: String): Optional<PaymentEntity>

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "event", type = Join.Type.LEFT_FETCH)
    fun findByClientId(clientId: String): Optional<PaymentEntity>

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "event", type = Join.Type.LEFT_FETCH)
    override fun findById(id: String): Optional<PaymentEntity>

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "event", type = Join.Type.LEFT_FETCH)
    fun findByEventIdAndUserId(eventId: String, userId: String): List<PaymentEntity>

    @Join(value = "user", type = Join.Type.LEFT_FETCH)
    @Join(value = "event", type = Join.Type.LEFT_FETCH)
    fun findByUserId(userId: String): List<PaymentEntity>
}
