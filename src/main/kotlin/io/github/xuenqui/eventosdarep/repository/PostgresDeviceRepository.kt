package io.github.xuenqui.eventosdarep.repository

import io.github.xuenqui.eventosdarep.repository.entities.DeviceEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PostgresDeviceRepository : CrudRepository<DeviceEntity, String>
