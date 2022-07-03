package io.github.xuenqui.eventosdarep.application.controllers

import com.google.gson.JsonSyntaxException
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import com.stripe.model.StripeObject
import com.stripe.net.Webhook
import io.github.xuenqui.eventosdarep.application.configs.StripeConfig
import io.github.xuenqui.eventosdarep.domain.exceptions.ApiException
import io.github.xuenqui.eventosdarep.domain.services.PaymentService
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

@Controller
class PaymentController(
    private val stripeConfig: StripeConfig,
    private val paymentService: PaymentService
) {

    @Post("/events/{eventId}/users/{userId}/payments/create")
    fun createPayment(
        @PathVariable("userId") userId: String,
        @PathVariable("eventId") eventId: String
    ): HttpResponse<Map<String, String>> {
        val id = paymentService.createPaymentIntent(userId, eventId)
        return HttpResponse.created(mapOf("id" to id))
    }

    @Post("/stripe/webhook")
    fun stripeWebhook(
        @Header("Stripe-Signature") signature: String,
        @Body payload: String
    ) {
        val event: Event

        try {
            event = Webhook.constructEvent(payload, signature, stripeConfig.endpointSecret)
        } catch (_: JsonSyntaxException) {
            throw ApiException("Invalid Json payload", HttpStatus.BAD_REQUEST)
        } catch (_: SignatureVerificationException) {
            throw ApiException("Invalid signature", HttpStatus.BAD_REQUEST)
        }

        val dataObjectDeserializer = event.dataObjectDeserializer
        val stripeObject: StripeObject

        if (dataObjectDeserializer.getObject().isPresent) {
            stripeObject = dataObjectDeserializer.getObject().get()
        } else {
            throw ApiException("Invalid webhook data", HttpStatus.BAD_REQUEST)
        }

        when (event.type) {
            "payment_intent.succeeded" -> {
                val paymentIntent = stripeObject as PaymentIntent
                paymentService.confirmPaymentAndJoinTheEvent(paymentIntent.clientSecret)
                logger.info("payment completed: ${paymentIntent.id}")
            }
            "payment_intent.payment_faile" -> {
                val paymentIntent = stripeObject as PaymentIntent
                paymentService.rejectPayment(paymentIntent.clientSecret)
                logger.info("payment failed: ${paymentIntent.id}")
            }
            else -> logger.info("unknown webhook event: ${event.type}")
        }
    }

    companion object : LoggableClass()
}
