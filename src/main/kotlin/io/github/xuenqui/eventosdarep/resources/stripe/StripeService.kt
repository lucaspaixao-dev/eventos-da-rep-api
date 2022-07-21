package io.github.xuenqui.eventosdarep.resources.stripe

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.model.Refund
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.RefundCreateParams
import io.github.xuenqui.eventosdarep.application.configs.StripeConfig
import jakarta.inject.Singleton

@Singleton
class StripeService(
    private val stripeConfig: StripeConfig
) {

    fun createPaymentIntent(amount: Long): PaymentIntentResponse {
        Stripe.apiKey = stripeConfig.apiKey

        val params = PaymentIntentCreateParams.builder()
            .setAmount(amount)
            .setCurrency("brl")
            .addPaymentMethodType("card")
            .build()

        val intent = PaymentIntent.create(params)
        return PaymentIntentResponse(
            id = intent.id,
            clientSecret = intent.clientSecret
        )
    }

    fun refund(paymentIntentId: String): String {
        Stripe.apiKey = stripeConfig.apiKey

        val params = RefundCreateParams.builder()
            .setPaymentIntent(paymentIntentId)
            .build()

        val refund = Refund.create(params)
        return refund.id
    }
}

data class PaymentIntentResponse(
    val id: String,
    val clientSecret: String
)
