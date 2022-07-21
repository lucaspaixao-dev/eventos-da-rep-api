package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.requests.CreatePaymentRequest
import io.github.xuenqui.eventosdarep.domain.PaymentIntent
import io.github.xuenqui.eventosdarep.domain.services.PaymentService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/payments")
@Secured(SecurityRule.IS_AUTHENTICATED)
class PaymentController(
    private val paymentService: PaymentService
) {

    @Post
    fun createPayment(
        @Body request: CreatePaymentRequest
    ): HttpResponse<Map<String, String>> {
        val id = paymentService.createPaymentIntent(request.userId, request.eventId)
        return HttpResponse.created(mapOf("id" to id))
    }

    @Get
    fun findByEventAndUser(
        @QueryValue("userId") userId: String,
        @QueryValue("eventId") eventId: String
    ): List<PaymentIntent> {
        return paymentService.findByEventAndUser(userId, eventId)
    }

    @Get("/user/{userId}")
    fun findByUser(
        @PathVariable("userId") userId: String
    ): List<PaymentIntent> {
        return paymentService.findByUser(userId)
    }


    @Put("/{paymentId}/refund")
    fun refund(
        @PathVariable("paymentId") paymentId: String
    ): HttpResponse<Nothing> {
        paymentService.refund(paymentId)
        return HttpResponse.noContent()
    }
}
