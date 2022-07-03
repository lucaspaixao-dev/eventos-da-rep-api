package io.github.xuenqui.eventosdarep.domain.jobs

import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton

@Singleton
class DeleteExpiredEventsJob(
    private val eventRepository: EventRepository
) {

    @Scheduled(cron = "0 0 * * *")
    fun execute() {
//        logger.info("deleting expired events")
//
//        val events = eventRepository.findALlWithoutPage()
//        val date = LocalDateTime.now().minusDays(1)
//
//        val expiredEvents = events.filter {
//            it.date.isBefore(date)
//        }.toList()
//
//        expiredEvents.forEach {
//            eventRepository.removeEventOnUsersEvents(it.id!!)
//        }
//        eventRepository.deleteInBatch(expiredEvents)
    }

    companion object : LoggableClass()
}
