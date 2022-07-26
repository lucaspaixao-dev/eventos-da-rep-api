package io.github.xuenqui.eventosdarep.domain

import java.time.LocalDateTime

data class Payment(
    val id: String? = null,
    val gatewayPaymentId: String,
    val gatewayPaymentIntentClientId: String,
    val amount: Long,
    val currency: Currency = Currency.BRL,
    val userId: String,
    val eventId: String,
    val eventName: String,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
    val payAt: LocalDateTime? = null
)
