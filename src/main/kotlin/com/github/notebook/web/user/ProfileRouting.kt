package com.github.notebook.web.user

import com.github.notebook.common.getClaim
import com.github.notebook.common.withRole
import com.github.notebook.model.Role
import com.github.notebook.plugins.API_AUTH
import com.github.notebook.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting() {
    authenticate(API_AUTH) {
        withRole(Role.USER) {
            route("/api/profile") {
                get {
                    val principalId: Int = call.getClaim("userId")
                    val user = UserService.get(principalId)
                    call.respond(HttpStatusCode.OK, user)
                }

                put {
                    UserService.update(call.receive(), call.getClaim("userName"))
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
