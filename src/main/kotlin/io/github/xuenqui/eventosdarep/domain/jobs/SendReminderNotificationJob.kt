package io.github.xuenqui.eventosdarep.domain.jobs

import io.github.xuenqui.eventosdarep.domain.services.NotificationService
import io.github.xuenqui.eventosdarep.logging.LoggableClass
import io.github.xuenqui.eventosdarep.resources.repository.EventRepository
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Singleton
class SendReminderNotificationJob(
    private val eventRepository: EventRepository,
    private val notificationService: NotificationService
) {

    @Scheduled(cron = "0 9 * * *")
    fun execute() {
        logger.info("Running events reminder job")

        val now = LocalDate.now()

        val events = eventRepository.findALlWithoutPage()
        val tomorrow = now.plusDays(1)
        val oneWeek = now.plusWeeks(1)

        val tomorrowEvents = events.filter {
            it.date
                .isEqual(tomorrow)
        }.toList()

        val oneWeekEvents = events.filter {
            it.date
                .isEqual(oneWeek)
        }.toList()

        if (tomorrowEvents.isNotEmpty()) {
            tomorrowEvents.forEach {
                val title = "FALTA UM DIA PARA O EVENTO ${it.title} 🥳"
                val body = "AMANHÃ TEM EVENTO DA REP! ESTÁ PREPARADO PARA O EVENTO AMANHÃ ÁS ${buildTime(it.begin)}? " +
                    "BORA SE DIVERTIR! 🤩"

                notificationService.sendNotificationToTopic(title, body, it.id!!)
            }
        }

        if (oneWeekEvents.isNotEmpty()) {
            oneWeekEvents.forEach {
                val title = "FALTA UMA SEMANA PARA O EVENTO ${it.title} 😍"
                val body = "E AI, ESTÁ ANCIOSO TAMBÉM PARA O EVENTO DA REP EM ${buildDate(it.date)}? 🤩"
                notificationService.sendNotificationToTopic(title, body, it.id!!)
            }
        }

        logger.info("Finishing the event reminder job")
    }

    private fun buildTime(begin: LocalTime): String {
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return begin.format(dtf)
    }

    private fun buildDate(date: LocalDate): String {
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(dtf)
    }

    companion object : LoggableClass()
}
