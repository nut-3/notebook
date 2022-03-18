package com.github.notebook.web.auth

import com.github.notebook.ServerTest
import com.github.notebook.USER_PASSWORD
import com.github.notebook.user
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AuthRoutingKtTest : ServerTest("") {

    @Test
    fun `Test Login page`() = runTest {

        client.submitForm("$rootPath/login") {
            setBody(
                FormDataContent(
                    parametersOf("username" to listOf(user.name), "password" to listOf(USER_PASSWORD))
                )
            )
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.OK)
            assertThat(this.bodyAsText()).contains("Hello, ${user.name}!")
        }
    }
}