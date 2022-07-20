package io.github.xuenqui.eventosdarep.application.controllers.requests

data class CreatePaymentRequest(
    val userId: String,
    val eventId: String
)
