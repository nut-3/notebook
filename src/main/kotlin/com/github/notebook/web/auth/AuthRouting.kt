package com.github.notebook.web.auth

import com.github.notebook.plugins.LOGIN_AUTH
import com.github.notebook.service.JwtService
import com.github.notebook.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

@JvmInline
value class AuthRouting(val root: String = "")

fun Route.authRouting(root: String = AuthRouting().root) {

    authenticate(LOGIN_AUTH) {
        post("$root/login") {
            val userName = call.principal<UserIdPrincipal>()!!.name
            val user = UserService.get(userName)
            val jwtToken = JwtService.generateJwt(user)
            call.respondText(
                "Hello, $userName!\n" +
                        "You JWT Token: $jwtToken"
            )
        }
    }

    get(root) {
        call.respondHtml {
            body {
                form(
                    action = "/login",
                    encType = FormEncType.applicationXWwwFormUrlEncoded,
                    method = FormMethod.post
                ) {
                    p {
                        +"Username:"
                        textInput(name = "username")
                    }
                    p {
                        +"Password:"
                        passwordInput(name = "password")
                    }
                    p {
                        submitInput { value = "Login" }
                    }
                }
            }
        }
    }
}