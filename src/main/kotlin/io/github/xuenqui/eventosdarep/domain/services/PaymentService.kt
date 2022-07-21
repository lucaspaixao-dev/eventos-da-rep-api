package io.github.xuenqui.eventosdarep.domain.services

import io.github.xuenqui.eventosdarep.domain.PaymentIntent
import io.github.xuenqui.eventosdarep.domain.PaymentStatus
import io.github.xuenqui.eventosdarep.domain.exceptions.BadRequestException
import io.github.xuenqui.eventosdarep.domain.exceptions.ResourceNotFoundException
import io.github.xuenqui.eventosdarep.resources.repository.PaymentRepository
import io.github.xuenqui.eventosdarep.resources.stripe.StripeService
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val eventService: EventService,
    private val userService: UserService,
    private val stripeService: StripeService
) {

    fun createPaymentIntent(userId: String, eventId: String): String {
        val event = eventService.findById(eventId)
        val user = userService.findById(userId)

        if (!event.isPayed) {
            throw BadRequestException("The event is free")
        }

        val payments = paymentRepository.findByEventAndUser(userId, eventId)

        payments.forEach {
            checkIsSuccess(it)
            checkIsProcessing(it)
        }

        val amount = event.amount ?: throw BadRequestException("The event has no amount")
        val response = stripeService.createPaymentIntent(amount)

        val paymentIntent = PaymentIntent(
            userId = userId,
            eventId = eventId,
            eventName = event.title,
            amount = amount,
            gatewayPaymentId = response.id,
            gatewayPaymentIntentClientId = response.clientSecret
        )

        paymentRepository.create(paymentIntent, user, event)
        return paymentIntent.gatewayPaymentIntentClientId
    }

    fun findByEventAndUser(eventId: String, userId: String) = paymentRepository.findByEventAndUser(eventId, userId)

    fun findByUser(userId: String) = paymentRepository.findByUser(userId)

    fun refund(paymentId: String) {
        val payment = paymentRepository.findById(paymentId) ?: throw ResourceNotFoundException("No payment found")

        if (payment.status != PaymentStatus.SUCCESS) {
            throw BadRequestException("The payment is not success")
        }

        stripeService.refund(payment.gatewayPaymentId)
    }

    fun confirmPaymentAndJoinTheEvent(paymentIntentClientId: String) {
        val paymentIntent = paymentRepository.findByPaymentIntentClientId(paymentIntentClientId)
            ?: throw ResourceNotFoundException("payment intent not found")

        if (paymentIntent.status == PaymentStatus.PENDING || paymentIntent.status == PaymentStatus.PROCESSING) {
            eventService.join(paymentIntent.eventId, paymentIntent.userId)

            val success = paymentIntent.copy(
                status = PaymentStatus.SUCCESS,
                payAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            paymentRepository.updateStatus(success)
        }
    }

    fun setRefundedAndRemoveFromEvent(gatewayPaymentId: String) {
        val paymentIntent = paymentRepository.findByGatewayPaymentId(gatewayPaymentId)
            ?: throw ResourceNotFoundException("payment intent not found")

        if (paymentIntent.status == PaymentStatus.PROCESSING || paymentIntent.status == PaymentStatus.SUCCESS) {
            eventService.remove(paymentIntent.eventId, paymentIntent.userId)

            val success = paymentIntent.copy(status = PaymentStatus.REFUNDED, updatedAt = LocalDateTime.now())
            paymentRepository.updateStatus(success)
        }
    }

    fun processing(paymentIntentClientId: String) {
        val paymentIntent = paymentRepository.findByPaymentIntentClientId(paymentIntentClientId)
            ?: throw ResourceNotFoundException("payment intent not found")

        if (paymentIntent.status == PaymentStatus.PENDING) {
            val success = paymentIntent.copy(status = PaymentStatus.PROCESSING, updatedAt = LocalDateTime.now())
            paymentRepository.updateStatus(success)
        }
    }

    fun rejectPayment(paymentIntentClientId: String) {
        val paymentIntent = paymentRepository.findByPaymentIntentClientId(paymentIntentClientId)
            ?: throw ResourceNotFoundException("payment intent not found")

        if (paymentIntent.status == PaymentStatus.PENDING || paymentIntent.status == PaymentStatus.PROCESSING) {
            val success = paymentIntent.copy(status = PaymentStatus.FAILED, updatedAt = LocalDateTime.now())
            paymentRepository.updateStatus(success)
        }
    }

    private fun checkIsProcessing(paymentIntent: PaymentIntent) {
        if (paymentIntent.status == PaymentStatus.PROCESSING) {
            throw BadRequestException("There is a processing payment for this event")
        }
    }

    private fun checkIsSuccess(paymentIntent: PaymentIntent) {
        if (paymentIntent.status == PaymentStatus.SUCCESS) {
            throw BadRequestException("The user already paid for this event")
        }
    }
}
