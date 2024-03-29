package io.github.xuenqui.eventosdarep.application.factories

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader
import jakarta.inject.Singleton

@Factory
class FirebaseFactory(
    private val firebaseProperty: FirebaseProperty
) {

    @Bean
    @Singleton
    fun firebaseMessaging(): FirebaseMessaging {
        val loader: ClassPathResourceLoader = ResourceResolver().getLoader(ClassPathResourceLoader::class.java).get()
        logger.info("loading firebase config: ${firebaseProperty.path}")
        val resource = loader.getResource(firebaseProperty.path)
        val googleCredentials = GoogleCredentials
            .fromStream(resource.get().openStream())

        val firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build()

        val app = FirebaseApp.initializeApp(firebaseOptions)
        return FirebaseMessaging.getInstance(app)
    }

    companion object : LoggableClass()
}

@ConfigurationProperties("firebase")
class FirebaseProperty {
    var path: String? = null
}
