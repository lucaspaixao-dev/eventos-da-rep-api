package io.github.xuenqui.eventosdarep.repository

import com.mongodb.client.model.Filters.eq
import io.github.xuenqui.eventosdarep.domain.User
import jakarta.inject.Singleton
import org.litote.kmongo.coroutine.CoroutineClient

@Singleton
class UserRepository(
    mongoClient: CoroutineClient
) {

    private val collection = mongoClient
        .getDatabase("eventos_da_rep")
        .getCollection<User>("users")

    suspend fun save(user: User) {
        collection.insertOne(user)
    }

    suspend fun findAll(): List<User> = collection.find().toList()

    suspend fun findById(id: String): User? =
        collection.find(
            eq("id", id)
        ).first()

    suspend fun findByEmail(email: String): User? =
        collection.find(
            eq("email", email)
        ).first()

    suspend fun update(user: User) {
        collection.replaceOne(
            eq("id", user.id),
            user
        )
    }
}
