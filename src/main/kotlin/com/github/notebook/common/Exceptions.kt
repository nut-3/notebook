package com.github.notebook.common

import io.ktor.http.*
import io.ktor.server.plugins.*

class ForbiddenException(message: String, cause: Throwable? = null) : BadRequestException(message, cause) {
    val httpStatus: HttpStatusCode = HttpStatusCode.Forbidden
}