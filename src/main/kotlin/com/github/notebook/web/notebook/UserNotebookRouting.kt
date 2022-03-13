package com.github.notebook.web.notebook

import com.github.notebook.common.withRole
import com.github.notebook.model.Role
import com.github.notebook.plugins.API_AUTH
import com.github.notebook.service.NotebookService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@JvmInline
value class UserNotebookRouting(val root: String = "/api/notebooks")

fun Route.notebookRouting() {
    authenticate(API_AUTH) {
        withRole(Role.USER) {
            route(UserNotebookRouting().root) {
                get {
                    val principal = call.authentication.principal<JWTPrincipal>()!!
                    val principalId = principal.getClaim("userId", Int::class)!!
                    val notebooks = NotebookService.getAllByUserId(principalId)
                    call.respond(HttpStatusCode.OK, notebooks)
                }

                get("/{id}") {
                    val principal = call.authentication.principal<JWTPrincipal>()!!
                    val principalId = principal.getClaim("userId", Int::class)!!
                    val notebookId = call.parameters["id"] ?: throw MissingRequestParameterException("id")
                    val notebook = NotebookService.getByIdAndUserId(notebookId.toInt(), principalId)
                    call.respond(HttpStatusCode.OK, notebook)
                }

                post {
                    val principal = call.authentication.principal<JWTPrincipal>()!!
                    val principalId = principal.getClaim("userId", Int::class)!!
                    NotebookService.createByIdAndUserId(call.receive(), principalId)
                    call.respond(HttpStatusCode.NoContent)
                }

                put("/{id}") {
                    val principal = call.authentication.principal<JWTPrincipal>()!!
                    val principalId = principal.getClaim("userId", Int::class)!!
                    val notebookId = call.parameters["id"] ?: throw MissingRequestParameterException("id")
                    NotebookService.deleteByIdAndUserId(notebookId.toInt(), principalId)
                    call.respond(HttpStatusCode.NoContent)
                }

                delete("/{id}") {
                    val principal = call.authentication.principal<JWTPrincipal>()!!
                    val principalId = principal.getClaim("userId", Int::class)!!
                    val notebookId = call.parameters["id"] ?: throw MissingRequestParameterException("id")
                    NotebookService.updateByIdAndUserId(call.receive(), notebookId.toInt(), principalId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}