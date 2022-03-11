package com.github.notebook.plugins

import com.github.notebook.web.auth.authRouting
import com.github.notebook.web.user.userAdminRouting
import com.github.notebook.web.user.userRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        authRouting()
        userRouting()
        userAdminRouting()
    }
}
