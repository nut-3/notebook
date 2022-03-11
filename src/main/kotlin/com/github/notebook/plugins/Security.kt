package com.github.notebook.plugins

import com.github.notebook.authorization.service.JwtService
import com.github.notebook.common.ForbiddenException
import com.github.notebook.user.model.Role
import com.github.notebook.user.model.Users
import com.github.notebook.user.model.Users.active
import com.github.notebook.user.model.Users.password
import com.github.notebook.user.service.PasswordService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

const val API_AUTH = "auth-jwt"
const val LOGIN_AUTH = "auth-form"

fun Application.configureSecurity() {

    // set environment for JWT generation and verification
    JwtService.setConfig(environment.config)

    install(Authentication) {
        form(name = LOGIN_AUTH) {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                val userName = credentials.name
                log.info("Auth {}", userName)
                val result = transaction {
                    addLogger(Slf4jSqlDebugLogger)
                    Users.slice(active, password).select { (Users.name eq userName) }.singleOrNull()
                        ?: throw ForbiddenException("User with name=$userName not found")
                }
                if (!result[active]) throw ForbiddenException("User $userName is inactive")
                if (PasswordService.verifyPassword(credentials.password, result[password])) {
                    UserIdPrincipal(userName)
                } else {
                    null
                }
            }
        }


        jwt(name = API_AUTH) {
            verifier(JwtService.getVerifier())

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun Route.withRole(role: Role, callback: Route.() -> Unit): Route {

    val routeWithRole = this.createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })

    routeWithRole.intercept(ApplicationCallPipeline.Call) {
        val principal = call.authentication.principal<JWTPrincipal>()
            ?: throw ForbiddenException("Missing principal")

        val principalRoles = principal.payload.getClaim("roles").asList(Role::class.java)
        if (!principalRoles.contains(role)) {
            throw ForbiddenException("Principal lacks required role $role")
        }
        proceed()
    }
    callback(routeWithRole)
    return routeWithRole
}