package com.github.notebook.plugins

import com.github.notebook.common.JsonMapper
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(JsonMapper)
    }
}
