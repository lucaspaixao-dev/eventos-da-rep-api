package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.domain.Invitation
import io.github.xuenqui.eventosdarep.domain.exceptions.RepositoryException
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.github.xuenqui.eventosdarep.resources.repository.entities.InvitationEntity
import io.micronaut.data.annotation.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
@SuppressWarnings("TooGenericExceptionCaught")
open class InvitationRepository(
    private val postgresInvitationRepository: PostgresInvitationRepository
) {

    fun create(email: String): String =
        try {
            logger.info("creating invitation: email=$email")
            val id = UUID.randomUUID().toString()
            val entity =
                InvitationEntity(id = id, email = email, createdAt = LocalDateTime.now())

            postgresInvitationRepository.save(entity)
            id
        } catch (e: Exception) {
            throw RepositoryException("error to create a invitation", e)
        }

    fun findByEmail(email: String): Invitation? =
        try {
            logger.info("finding invitation by email: email=$email")
            postgresInvitationRepository.findByEmail(email).map { it.toDomain() }.orElse(null)
        } catch (e: Exception) {
            throw RepositoryException("error to find a invitation by email", e)
        }

    fun delete(email: String) {
        try {
            logger.info("deleting invitation by email: email=$email")
            postgresInvitationRepository.deleteByEmail(email)
        } catch (e: Exception) {
            throw RepositoryException("error to delete a invitation by email", e)
        }
    }

    companion object : LoggableClass()
}

fun InvitationEntity.toDomain() =
    Invitation(
        id = id,
        email = email,
        createdAt = createdAt
    )
