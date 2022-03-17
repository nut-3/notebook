package com.github.notebook.web.notebook.note

import com.github.notebook.common.getClaim
import com.github.notebook.common.withRole
import com.github.notebook.model.NewNote
import com.github.notebook.model.Role
import com.github.notebook.plugins.API_AUTH
import com.github.notebook.service.NoteService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.noteRouting() {
    authenticate(API_AUTH) {
        withRole(Role.USER) {
            route("/api/notebooks/{notebookId}/notes") {
                get {
                    val notebookId = call.parameters.getOrFail("notebookId").toInt()
                    val notes = NoteService.getAll(notebookId, call.getClaim("userId"))
                    call.respond(HttpStatusCode.OK, notes)
                }

                get("/{id}") {
                    val notebookId = call.parameters.getOrFail("notebookId").toInt()
                    val noteId = call.parameters.getOrFail("id").toInt()
                    val note = NoteService.get(noteId, notebookId, call.getClaim("userId"))
                    call.respond(HttpStatusCode.OK, note)
                }

                post {
                    val notebookId = call.parameters.getOrFail("notebookId").toInt()
                    val newNote = call.receive<NewNote>()
                    call.respond(
                        HttpStatusCode.OK,
                        NoteService.create(newNote, notebookId, call.getClaim("userId"))
                    )
                }

                put("/{id}") {
                    val notebookId = call.parameters.getOrFail("notebookId").toInt()
                    val noteId = call.parameters.getOrFail("id").toInt()
                    NoteService.update(call.receive(), noteId, notebookId, call.getClaim("userId"))
                    call.respond(HttpStatusCode.NoContent)
                }

                delete("/{id}") {
                    val notebookId = call.parameters.getOrFail("notebookId").toInt()
                    val noteId = call.parameters.getOrFail("id").toInt()
                    NoteService.delete(noteId, notebookId, call.getClaim("userId"))
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}