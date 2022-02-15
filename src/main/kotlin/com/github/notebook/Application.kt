package com.github.notebook

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.github.notebook.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureMonitoring()
        configureHTTP()
        configureSecurity()
    }.start(wait = true)
}
