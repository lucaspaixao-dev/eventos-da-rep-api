package io.github.xuenqui.eventosdarep.repository

import com.mongodb.client.model.Filters
import io.github.xuenqui.eventosdarep.domain.Event
import jakarta.inject.Singleton
import java.util.UUID
import org.litote.kmongo.coroutine.CoroutineClient

@Singleton
class EventRepository(
    mongoClient: CoroutineClient
) {

    private val collection = mongoClient
        .getDatabase("eventos_da_rep")
        .getCollection<Event>("events")

    suspend fun save(event: Event): String {
        val id = UUID.randomUUID().toString()
        val newEvent = event.copy(id = id)

        collection.insertOne(newEvent)
        return id
    }

    suspend fun findAll(): List<Event> = collection.find().toList()

    suspend fun findById(id: String): Event? =
        collection.find(
            Filters.eq("id", id)
        ).first()

    suspend fun update(event: Event) {
        collection.replaceOne(
            Filters.eq("id", event.id),
            event
        )
    }
}
