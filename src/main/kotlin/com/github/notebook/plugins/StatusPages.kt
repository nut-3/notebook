package com.github.notebook.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import org.valiktor.ConstraintViolationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is ConstraintViolationException -> {
                    val detailMessage =
                        cause.constraintViolations.joinToString("\n") { "[${it.property}] - ${it.constraint.name}${it.constraint.messageParams}" }
                    call.respondText(
                        text = "${HttpStatusCode.UnprocessableEntity.value}: $detailMessage",
                        status = HttpStatusCode.UnprocessableEntity
                    )
                }
                is UnsupportedMediaTypeException -> call.respondText(
                    text = "${HttpStatusCode.UnsupportedMediaType.value}: $cause",
                    status = HttpStatusCode.UnsupportedMediaType
                )
                is BadRequestException -> call.respondText(
                    text = "${HttpStatusCode.BadRequest.value}: $cause",
                    status = HttpStatusCode.BadRequest
                )
                is NotFoundException -> call.respondText(
                    text = "${HttpStatusCode.NotFound.value}: $cause",
                    status = HttpStatusCode.NotFound
                )
                else -> call.respondText(
                    text = "${HttpStatusCode.InternalServerError.value}: $cause",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}