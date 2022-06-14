package io.github.xuenqui.eventosdarep.application.handler

data class ExceptionResponse(
    val message: String,
    val details: Map<String, List<String>>? = null
)
