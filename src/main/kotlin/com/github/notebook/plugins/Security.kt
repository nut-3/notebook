package com.github.notebook.plugins

import com.github.notebook.common.ForbiddenException
import com.github.notebook.model.Users.active
import com.github.notebook.model.Users.password
import com.github.notebook.service.JwtService
import com.github.notebook.service.PasswordService
import com.github.notebook.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

const val API_AUTH = "auth-jwt"
const val LOGIN_AUTH = "auth-form"

fun Application.configureSecurity() {

    // set environment config for JWT generation and verification
    JwtService.setConfig(environment.config)

    install(Authentication) {
        form(name = LOGIN_AUTH) {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                val userName = credentials.name
                this@configureSecurity.log.info("Auth $userName")
                val result = UserService.getUserStatus(userName)
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
                if (credential.payload.getClaim("userName").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}