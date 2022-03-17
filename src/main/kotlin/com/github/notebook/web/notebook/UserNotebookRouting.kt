package com.github.notebook.web.notebook

import com.github.notebook.common.getClaim
import com.github.notebook.common.withRole
import com.github.notebook.model.Role
import com.github.notebook.plugins.API_AUTH
import com.github.notebook.service.NotebookService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.notebookRouting() {
    authenticate(API_AUTH) {
        withRole(Role.USER) {
            route("/api/notebooks") {
                get {
                    val notebooks = NotebookService.getAll(call.getClaim("userId"))
                    call.respond(HttpStatusCode.OK, notebooks)
                }

                get("/{id}") {
                    val notebookId = call.parameters.getOrFail("id")
                    val notebook = NotebookService.get(notebookId.toInt(), call.getClaim("userId"))
                    call.respond(HttpStatusCode.OK, notebook)
                }

                post {
                    NotebookService.create(call.receive(), call.getClaim("userId"))
                    call.respond(HttpStatusCode.NoContent)
                }

                put("/{id}") {
                    val notebookId = call.parameters.getOrFail("id")
                    NotebookService.delete(notebookId.toInt(), call.getClaim("userId"))
                    call.respond(HttpStatusCode.NoContent)
                }

                delete("/{id}") {
                    val notebookId = call.parameters.getOrFail("id")
                    NotebookService.update(call.receive(), notebookId.toInt(), call.getClaim("userId"))
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}