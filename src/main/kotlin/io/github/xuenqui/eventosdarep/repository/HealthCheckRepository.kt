package io.github.xuenqui.eventosdarep.repository

import jakarta.inject.Singleton
import org.litote.kmongo.coroutine.CoroutineClient

@Singleton
class HealthCheckRepository(
    mongoClient: CoroutineClient
) {

    private val database = mongoClient
        .getDatabase("eventos_da_rep")

    suspend fun ping() = database.listCollectionNames()
}
