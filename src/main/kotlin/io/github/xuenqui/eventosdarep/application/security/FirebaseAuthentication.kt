package io.github.xuenqui.eventosdarep.application.security

import com.google.firebase.auth.FirebaseToken
import io.micronaut.security.authentication.Authentication
import java.util.Collections

class FirebaseAuthentication(
    private val firebaseToken: FirebaseToken
) : Authentication {

    override fun getAttributes(): MutableMap<String, Any> =
        Collections.unmodifiableMap(firebaseToken.claims)

    override fun getName(): String = firebaseToken.uid
}
