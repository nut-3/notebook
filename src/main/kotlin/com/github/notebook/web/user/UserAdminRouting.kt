package com.github.notebook.web.user

import com.github.notebook.common.withRole
import com.github.notebook.model.NewUser
import com.github.notebook.model.Role
import com.github.notebook.plugins.API_AUTH
import com.github.notebook.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.userAdminRouting() {
    authenticate(API_AUTH) {
        withRole(Role.ADMIN) {
            route("/api/admin/users") {
                get {
                    call.respond(HttpStatusCode.OK, UserService.getAll())
                }

                get("/{name}") {
                    val userName = call.parameters.getOrFail("name")
                    val user = UserService.get(userName)
                    call.respond(HttpStatusCode.OK, user)
                }

                post {
                    val user = call.receive<NewUser>()
                    call.respond(HttpStatusCode.Created, UserService.create(user))
                }

                put("/{name}") {
                    val userName = call.parameters.getOrFail("name")
                    UserService.update(call.receive(), userName)
                    call.respond(HttpStatusCode.NoContent)
                }

                delete("/{name}") {
                    val userName = call.parameters.getOrFail("name")
                    UserService.delete(userName)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
