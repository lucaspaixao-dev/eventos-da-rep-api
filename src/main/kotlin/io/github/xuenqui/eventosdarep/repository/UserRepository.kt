package io.github.xuenqui.eventosdarep.repository

import io.github.xuenqui.eventosdarep.domain.Device
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.repository.entities.DeviceEntity
import io.github.xuenqui.eventosdarep.repository.entities.UserEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import java.time.LocalDateTime
import java.util.UUID

@Repository
open class UserRepository(
    private val postgresUserRepository: PostgresUserRepository,
    private val postgresDeviceRepository: PostgresDeviceRepository
) {

    fun findAll(page: Int, size: Int): List<User> {
        val sort = Sort.UNSORTED.order(Sort.Order.asc("name"))
        val pageable = Pageable.from(page, size, sort)

        return postgresUserRepository.findAll(pageable).map { it.toDomain() }.toList()
    }

    fun findByEmail(email: String): User? =
        postgresUserRepository.findByEmail(email).orElse(null)?.toDomain()

    fun findById(id: String): User? =
        postgresUserRepository.findById(id).orElse(null)?.toDomain()

    fun create(user: User): String {
        val userId = UUID.randomUUID().toString()

        val deviceEntity = DeviceEntity(
            id = UUID.randomUUID().toString(),
            device = user.device!!,
            createdAt = LocalDateTime.now(),
        )

        postgresDeviceRepository.save(deviceEntity)

        val userEntity = UserEntity(
            id = userId,
            user = user,
            device = deviceEntity,
            createdAt = LocalDateTime.now()
        )

        postgresUserRepository.save(userEntity)
        return userId
    }

    fun update(user: User): User {
        val deviceEntity = DeviceEntity(
            id = UUID.randomUUID().toString(),
            device = user.device!!,
            createdAt = user.device.createdAt ?: LocalDateTime.now(),
            updatedAt = user.device.updatedAt ?: LocalDateTime.now()
        )

        postgresDeviceRepository.update(deviceEntity)

        val userEntity = UserEntity(
            id = user.id!!,
            user = user,
            device = deviceEntity,
            createdAt = user.createdAt ?: LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return postgresUserRepository.update(userEntity).toDomain()
    }
}

fun UserEntity.toDomain() = User(
    id = this.id,
    name = this.name,
    email = this.email,
    photo = this.photo,
    isAdmin = this.isAdmin,
    authenticationId = this.authenticationId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    device = this.device?.toDomain()
)

fun DeviceEntity.toDomain() = Device(
    id = this.id,
    brand = this.brand,
    model = this.model,
    token = this.token,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun User.toEntity() = UserEntity(
    id = this.id,
    name = this.name,
    email = this.email,
    photo = this.photo,
    isAdmin = this.isAdmin,
    authenticationId = this.authenticationId,
    createdAt = this.createdAt ?: LocalDateTime.now(),
    updatedAt = this.updatedAt,
    device = this.device?.toEntity()
)

fun Device.toEntity() = DeviceEntity(
    id = this.id,
    brand = this.brand,
    model = this.model,
    token = this.token,
    createdAt = this.createdAt ?: LocalDateTime.now(),
    updatedAt = this.updatedAt ?: LocalDateTime.now()
)
