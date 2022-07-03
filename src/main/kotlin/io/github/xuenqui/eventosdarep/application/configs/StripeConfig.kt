package io.github.xuenqui.eventosdarep.application.configs

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("stripe")
class StripeConfig {
    var apiKey: String? = null
    var endpointSecret: String? = null
}
