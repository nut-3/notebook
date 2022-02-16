package com.github.notebook.plugins

import com.github.notebook.user.web.userRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        userRouting()
    }
}
