package com.github.notebook.user.web

import com.github.notebook.user.model.Users
import com.github.notebook.user.model.toUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.userRouting() {

    val ROUTING_ROOT = "/api/users"

    route(ROUTING_ROOT) {
        get {
            call.respond(
                transaction {
                    addLogger(StdOutSqlLogger)
                    return@transaction Users.selectAll().map { Users.toUser(it) }
                }
            )
        }
        get("{name}") {
            val userName = call.parameters["name"] ?: return@get call.respondText(
                "Missing or malformed user name",
                status = HttpStatusCode.BadRequest
            )
            call.respond(
                transaction {
                    addLogger(StdOutSqlLogger)
                    return@transaction Users.select { (Users.name eq userName) }.map { Users.toUser(it) }
                }
            )
        }
    }
}
