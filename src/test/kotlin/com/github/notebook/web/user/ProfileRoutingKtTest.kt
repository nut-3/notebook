package com.github.notebook.web.user

import com.github.notebook.ServerTest
import com.github.notebook.common.deSerialize
import com.github.notebook.common.serialize
import com.github.notebook.model.User
import com.github.notebook.service.UserService
import com.github.notebook.toUser
import com.github.notebook.updatedUser
import com.github.notebook.user
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProfileRoutingKtTest : ServerTest("/api/profile") {

    @Test
    fun `Test API get profile for current user`() = runTest {

        client.get(rootPath) {
            bearerAuth(userJWT)
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.OK)
            assertThat(deSerialize<User>(this.bodyAsText())).usingRecursiveComparison(comparisonConfiguration)
                .isEqualTo(user)
        }
    }

    @Test
    fun `Test API edit current user`() = runTest {

        client.put(rootPath) {
            bearerAuth(userJWT)
            contentType(ContentType.Application.Json)
            setBody(serialize(updatedUser))
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.NoContent)
            val userInDb = UserService.get(updatedUser.name)
            assertThat(userInDb).usingRecursiveComparison(comparisonConfiguration).isEqualTo(updatedUser.toUser())
        }
    }
}