package com.github.notebook.common

import com.github.notebook.model.Role
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

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

inline fun <reified T> ApplicationCall.getClaim(name: String): T {
    val principal = authentication.principal<JWTPrincipal>()!!
    return (principal[name] as T)!!
}