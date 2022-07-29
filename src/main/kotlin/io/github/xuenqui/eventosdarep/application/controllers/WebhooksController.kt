package io.github.xuenqui.eventosdarep.application.controllers

import com.google.gson.JsonSyntaxException
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Charge
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import com.stripe.model.StripeObject
import com.stripe.net.Webhook
import io.github.xuenqui.eventosdarep.application.configs.StripeConfig
import io.github.xuenqui.eventosdarep.domain.exceptions.ApiException
import io.github.xuenqui.eventosdarep.domain.services.PaymentService
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
class WebhooksController(
    private val stripeConfig: StripeConfig,
    private val paymentService: PaymentService
) {

    @Post("/stripe/webhook")
    @SuppressWarnings("ThrowsCount")
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
            "charge.refunded" -> {
                val charge = stripeObject as Charge
                paymentService.setRefundedAndRemoveFromEvent(charge.paymentIntent)
                logger.info("payment refunded: ${charge.paymentIntent}")
            }
            "payment_intent.succeeded" -> {
                val paymentIntent = stripeObject as PaymentIntent
                paymentService.confirmPaymentAndJoinTheEvent(paymentIntent.clientSecret)
                logger.info("payment completed: ${paymentIntent.id}")
            }
            "payment_intent.processing" -> {
                val paymentIntent = stripeObject as PaymentIntent
                paymentService.processing(paymentIntent.clientSecret)
                logger.info("payment processing: ${paymentIntent.id}")
            }
            "payment_intent.payment_failed", "payment_intent.canceled" -> {
                val paymentIntent = stripeObject as PaymentIntent
                paymentService.rejectPayment(paymentIntent.clientSecret)
                logger.info("payment failed: ${paymentIntent.id}")
            }
            else -> logger.info("unknown webhook event: ${event.type}")
        }
    }

    companion object : LoggableClass()
}
