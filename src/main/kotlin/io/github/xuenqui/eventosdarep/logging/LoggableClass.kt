package io.github.xuenqui.eventosdarep.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class LoggableClass {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
}
