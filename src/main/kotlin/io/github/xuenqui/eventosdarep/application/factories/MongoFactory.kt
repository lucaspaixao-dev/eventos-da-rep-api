package io.github.xuenqui.eventosdarep.application.factories

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import org.bson.codecs.configuration.CodecRegistry
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.id.jackson.IdJacksonModule
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.service.ClassMappingType
import org.litote.kmongo.util.CollectionNameFormatter

@Factory
@Requires(classes = [KMongo::class])
class MongoFactory {

    init {
        CollectionNameFormatter.useLowerCaseCollectionNameBuilder()
    }

    @Singleton
    fun kCodecRegistry(): CodecRegistry =
        ClassMappingType.codecRegistry(MongoClientSettings.getDefaultCodecRegistry())

    @Singleton
    fun coroutineClient(client: MongoClient): CoroutineClient = client.coroutine

    @Singleton
    fun idJacksonModule(): Module = IdJacksonModule()

    @Singleton
    fun javaTimeJacksonModule(): Module = JavaTimeModule()
}
