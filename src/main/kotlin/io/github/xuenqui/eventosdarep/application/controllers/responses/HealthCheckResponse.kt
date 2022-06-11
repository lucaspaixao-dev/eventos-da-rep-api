package io.github.xuenqui.eventosdarep.application.controllers.responses

data class HealthCheckResponse(
    val application: Status,
    val databaseStatus: Status
)

enum class Status {
    UP,
    DOWN
}
