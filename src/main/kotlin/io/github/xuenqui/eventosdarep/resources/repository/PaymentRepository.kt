package io.github.xuenqui.eventosdarep.resources.repository

import io.github.xuenqui.eventosdarep.domain.Currency
import io.github.xuenqui.eventosdarep.domain.Event
import io.github.xuenqui.eventosdarep.domain.PaymentIntent
import io.github.xuenqui.eventosdarep.domain.PaymentStatus
import io.github.xuenqui.eventosdarep.domain.User
import io.github.xuenqui.eventosdarep.domain.exceptions.RepositoryException
import io.github.xuenqui.eventosdarep.resources.repository.entities.EventEntity
import io.github.xuenqui.eventosdarep.resources.repository.entities.PaymentEntity
import io.github.xuenqui.eventosdarep.resources.repository.entities.UserEntity
import io.micronaut.data.annotation.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
@SuppressWarnings("TooGenericExceptionCaught")
open class PaymentRepository(
    private val postgresPaymentRepository: PostgresPaymentRepository
) {

    fun create(paymentIntent: PaymentIntent, user: User, event: Event) {
        try {
            val userEntity = UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                isAdmin = user.isAdmin,
                photo = user.photo,
                createdAt = user.createdAt!!,
                updatedAt = user.updatedAt
            )

            val eventEntity = EventEntity(
                id = event.id,
                title = event.title,
                latitude = event.latitude,
                longitude = event.longitude,
                city = event.city,
                address = event.address,
                description = event.description,
                photo = event.photo,
                date = event.date,
                begin = event.begin,
                end = event.end,
                active = event.active,
                createdAt = event.createdAt,
                updatedAt = event.updatedAt
            )

            val id = UUID.randomUUID().toString()
            val paymentEntity = PaymentEntity(
                id = id,
                amount = paymentIntent.amount,
                currency = paymentIntent.currency.name,
                clientId = paymentIntent.gatewayPaymentId,
                intentClientId = paymentIntent.gatewayPaymentIntentClientId,
                user = userEntity,
                event = eventEntity,
                status = paymentIntent.status.name,
                createdAt = LocalDateTime.now(),
                payAt = null
            )

            postgresPaymentRepository.save(paymentEntity)
        } catch (e: Exception) {
            throw RepositoryException("error creating payment", e)
        }
    }

    fun findByPaymentIntentClientId(paymentIntentClientId: String): PaymentIntent? {
        try {
            return postgresPaymentRepository.findByIntentClientId(paymentIntentClientId).map {
                PaymentIntent(
                    id = it.id,
                    amount = it.amount,
                    currency = Currency.valueOf(it.currency),
                    gatewayPaymentId = it.clientId,
                    gatewayPaymentIntentClientId = it.intentClientId,
                    status = PaymentStatus.valueOf(it.status),
                    eventId = it.event!!.id!!,
                    eventName = it.event.title,
                    userId = it.user!!.id!!,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    payAt = it.payAt
                )
            }.orElse(null)
        } catch (e: Exception) {
            throw RepositoryException("error finding payment", e)
        }
    }

    fun findByGatewayPaymentId(gatewayPaymentId: String): PaymentIntent? {
        try {
            return postgresPaymentRepository.findByClientId(gatewayPaymentId).map {
                PaymentIntent(
                    id = it.id,
                    amount = it.amount,
                    currency = Currency.valueOf(it.currency),
                    gatewayPaymentId = it.clientId,
                    gatewayPaymentIntentClientId = it.intentClientId,
                    status = PaymentStatus.valueOf(it.status),
                    eventId = it.event!!.id!!,
                    eventName = it.event.title,
                    userId = it.user!!.id!!,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    payAt = it.payAt
                )
            }.orElse(null)
        } catch (e: Exception) {
            throw RepositoryException("error finding payment", e)
        }
    }

    fun findById(paymentId: String) =
        try {
            postgresPaymentRepository.findById(paymentId).map {
                val paymentIntent = PaymentIntent(
                    id = it.id,
                    amount = it.amount,
                    currency = Currency.valueOf(it.currency),
                    gatewayPaymentId = it.clientId,
                    gatewayPaymentIntentClientId = it.intentClientId,
                    status = PaymentStatus.valueOf(it.status),
                    eventId = it.event!!.id!!,
                    eventName = it.event.title,
                    userId = it.user!!.id!!,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    payAt = it.payAt
                )
                paymentIntent
            }.orElse(null)
        } catch (e: Exception) {
            throw RepositoryException("error finding payment", e)
        }

    fun findByEventAndUser(userId: String, eventId: String): List<PaymentIntent> =
        try {
            postgresPaymentRepository.findByEventIdAndUserId(eventId, userId).map {
                PaymentIntent(
                    id = it.id,
                    amount = it.amount,
                    currency = Currency.valueOf(it.currency),
                    gatewayPaymentId = it.clientId,
                    gatewayPaymentIntentClientId = it.intentClientId,
                    status = PaymentStatus.valueOf(it.status),
                    eventId = it.event!!.id!!,
                    eventName = it.event.title,
                    userId = it.user!!.id!!,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    payAt = it.payAt
                )
            }.toList()
        } catch (e: Exception) {
            throw RepositoryException("error finding payments", e)
        }

    fun findByUser(userId: String): List<PaymentIntent> =
        try {
            postgresPaymentRepository.findByUserId(userId).map {
                PaymentIntent(
                    id = it.id,
                    amount = it.amount,
                    currency = Currency.valueOf(it.currency),
                    gatewayPaymentId = it.clientId,
                    gatewayPaymentIntentClientId = it.intentClientId,
                    status = PaymentStatus.valueOf(it.status),
                    eventId = it.event!!.id!!,
                    eventName = it.event.title,
                    userId = it.user!!.id!!,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    payAt = it.payAt
                )
            }.toList()
        } catch (e: Exception) {
            throw RepositoryException("error finding payments", e)
        }

    fun updateStatus(paymentIntent: PaymentIntent) {
        try {
            val entity = postgresPaymentRepository.findById(paymentIntent.id!!).get()
            val newEntity = entity.copy(
                status = paymentIntent.status.name,
                payAt = paymentIntent.payAt,
                updatedAt = LocalDateTime.now()
            )

            postgresPaymentRepository.update(newEntity)
        } catch (e: Exception) {
            throw RepositoryException("error updating payment", e)
        }
    }
}
