package com.github.notebook.user.web

import com.github.notebook.plugins.API_AUTH
import com.github.notebook.user.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@JvmInline
value class ProfileRouting(val root: String = "/api/profile")

fun Route.userRouting() {
    authenticate(API_AUTH) {
        route(ProfileRouting().root) {
            get {
                val principal = call.authentication.principal<JWTPrincipal>()!!
                val user = UserService.get(principal["username"]!!)
                call.respond(HttpStatusCode.OK, user)
            }

            put {
                val principal = call.authentication.principal<JWTPrincipal>()!!
                UserService.update(call.receive(), principal["username"]!!)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}