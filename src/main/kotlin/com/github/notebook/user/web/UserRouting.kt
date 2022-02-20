package com.github.notebook.user.web

import com.github.notebook.user.model.NewUser
import com.github.notebook.user.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val ROUTING_ROOT = "/api/users"

fun Route.userRouting() {

    route(ROUTING_ROOT) {
        get {
            call.respond(HttpStatusCode.OK, UserService.getAll())
        }

        get("/{name}") {
            val userName = call.parameters["name"] ?: throw MissingRequestParameterException("name")
            val user = UserService.get(userName)
            call.respond(HttpStatusCode.OK, user)
        }

        post {
            val user = call.receive<NewUser>()
            call.respond(HttpStatusCode.Created, UserService.create(user))
        }

        put("/{name}") {
            val userName = call.parameters["name"] ?: throw MissingRequestParameterException("name")
            UserService.update(call.receive(), userName)
            call.respond(HttpStatusCode.NoContent)
        }

        delete("/{name}") {
            val userName = call.parameters["name"] ?: throw MissingRequestParameterException("name")
            UserService.delete(userName)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
