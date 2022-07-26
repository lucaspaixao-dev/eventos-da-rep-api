package io.github.xuenqui.eventosdarep.domain.jobs

import io.github.xuenqui.eventosdarep.domain.exceptions.RepositoryException
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import io.github.xuenqui.eventosdarep.resources.repository.MessageRepository
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class CleanExpiredEventsJob(
    private val eventRepository: EventRepository,
    private val messageRepository: MessageRepository
) {

    @Scheduled(cron = "0 8 * * *")
    fun execute() {
        logger.info("deleting expired events")

        try {
            val events = eventRepository.findAllWithoutPage()
            val yesterday = LocalDate.now().minusDays(1)

            val expiredEvents = events.filter {
                it.date.isEqual(yesterday)
            }.toList()

            expiredEvents.forEach {
                val eventId = it.id!!

                logger.info("removing users on event ${it.title}")
                eventRepository.deleteUsersOnEvent(eventId)

                logger.info("removing messages")
                messageRepository.deleteByEvent(eventId)

                logger.info("removing event")
                eventRepository.delete(eventId)
            }
        } catch (e: RepositoryException) {
            logger.error("error to clean expired events: ", e)
        }
    }

    companion object : LoggableClass()
}
