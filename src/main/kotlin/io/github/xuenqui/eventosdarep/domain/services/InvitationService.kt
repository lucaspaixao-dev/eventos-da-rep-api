package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.Invitation
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceAlreadyExistsException
import io.github.xuenqui.eventosdarep.resources.repository.InvitationRepository
import jakarta.inject.Singleton

@Singleton
class InvitationService(
    private val invitationRepository: InvitationRepository
) {

    fun create(email: String): String {
        invitationRepository.findByEmail(email)?.let {
            throw ResourceAlreadyExistsException("Invitation already exists")
        }

        return invitationRepository.create(email)
    }

    fun findByEmail(email: String): Invitation? = invitationRepository.findByEmail(email)

    fun delete(email: String) {
        invitationRepository.findByEmail(email)?.run {
            invitationRepository.delete(email)
        }
    }
}
