package com.github.notebook.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import org.valiktor.ConstraintViolationException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is ConstraintViolationException -> {
                    val detailMessage =
                        cause.constraintViolations.map { "[${it.property}] - ${it.constraint.name}${it.constraint.messageParams}" }
                            .toList()
                    call.respond(ResponseEntity(HttpStatusCode.UnprocessableEntity, detailMessage))
                }
                is UnsupportedMediaTypeException -> call.respond(
                    ResponseEntity(HttpStatusCode.UnsupportedMediaType, listOf(cause.message.toString()))
                )
                is BadRequestException -> call.respond(
                    ResponseEntity(HttpStatusCode.BadRequest, listOf(cause.message.toString()))
                )
                is NotFoundException -> call.respond(
                    ResponseEntity(HttpStatusCode.NotFound, listOf(cause.message.toString()))
                )
                else -> call.respond(ResponseEntity(HttpStatusCode.InternalServerError, listOf(cause.message.toString())))
            }
        }
    }
}

private suspend fun ApplicationCall.respond(body: ResponseEntity) {
    this.respond(body.httpStatus, body)
}

@Suppress("unused")
@kotlinx.serialization.Serializable
class ResponseEntity private constructor(
    private val timestamp: String,
    private val status: Int,
    private val error: String,
    private val message: List<String>,
    @kotlinx.serialization.Transient
    val httpStatus: HttpStatusCode = HttpStatusCode.OK // dummy initialization
) {
    constructor(httpStatus: HttpStatusCode, message: List<String>) : this(
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
        status = httpStatus.value,
        error = httpStatus.description,
        message = message,
        httpStatus = httpStatus
    )
}