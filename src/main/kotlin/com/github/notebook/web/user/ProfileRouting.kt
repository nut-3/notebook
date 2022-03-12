package com.github.notebook.web.user

import com.github.notebook.model.Role
import com.github.notebook.plugins.API_AUTH
import com.github.notebook.plugins.withRole
import com.github.notebook.service.UserService
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
        withRole(Role.USER) {
            route(ProfileRouting().root) {
                get {
                    val principal = call.authentication.principal<JWTPrincipal>()!!
                    val user = UserService.get(principal["userName"]!!)
                    call.respond(HttpStatusCode.OK, user)
                }

                put {
                    val principal = call.authentication.principal<JWTPrincipal>()!!
                    UserService.update(call.receive(), principal["userName"]!!)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
