package io.github.xuenqui.eventosdarep.application.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.validator.TokenValidator
import io.reactivex.rxjava3.core.Flowable
import jakarta.inject.Singleton
import org.reactivestreams.Publisher

@Singleton
class FirebaseTokenValidator : TokenValidator {

    override fun validateToken(token: String?, request: HttpRequest<*>?): Publisher<Authentication> {
        return try {
            val firebaseToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
            Flowable.just(FirebaseAuthentication(firebaseToken))
        } catch (_: FirebaseAuthException) {
            Flowable.empty()
        }
    }
}
