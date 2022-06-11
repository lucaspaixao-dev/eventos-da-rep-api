package io.github.xuenqui.eventosdarep.application.controllers

import io.github.xuenqui.eventosdarep.application.controllers.responses.HealthCheckResponse
import io.github.xuenqui.eventosdarep.application.controllers.responses.Status
import io.github.xuenqui.eventosdarep.repository.HealthCheckRepository
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/health")
class HealthCheckController(
    private val healthCheckRepository: HealthCheckRepository
) {

    @Get
    suspend fun healthCheck(): HealthCheckResponse {
        return try {
            healthCheckRepository.ping()
            HealthCheckResponse(
                application = Status.UP,
                databaseStatus = Status.UP
            )
        } catch (e: Exception) {
            print(e)
            HealthCheckResponse(
                application = Status.UP,
                databaseStatus = Status.DOWN
            )
        }
    }
}
