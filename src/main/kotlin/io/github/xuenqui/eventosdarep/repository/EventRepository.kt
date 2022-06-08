package io.github.xuenqui.eventosdarep.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters
import io.github.xuenqui.eventosdarep.domain.Event
import jakarta.inject.Singleton

@Singleton
class EventRepository(
    mongoClient: MongoClient
) {

    private val collection = mongoClient
        .getDatabase("eventos_da_rep")
        .getCollection("events", Event::class.java)

    fun save(event: Event) {
        collection.insertOne(event)
    }

    fun findAll(): List<Event> = collection.find().toList()

    fun findById(id: String): Event? =
        collection.find(
            Filters.eq("id", id)
        ).first()

    fun update(event: Event) {
        collection.replaceOne(
            Filters.eq("id", event.id),
            event
        )
    }
}
