package com.github.notebook.plugins

import com.github.notebook.authorization.web.authRouting
import com.github.notebook.user.web.userAdminRouting
import com.github.notebook.user.web.userRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        authRouting()
        userRouting()
        userAdminRouting()
    }
}
