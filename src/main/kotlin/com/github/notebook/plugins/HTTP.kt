package com.github.notebook.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun Application.configureHTTP() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        hostIsIp("127.0.0.1")
    }

}
