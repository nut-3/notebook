package com.github.notebook.web.user

import com.github.notebook.*
import com.github.notebook.common.deSerialize
import com.github.notebook.common.serialize
import com.github.notebook.model.User
import com.github.notebook.service.UserService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class UserAdminRoutingKtTest : ServerTest() {

    @Test
    fun `Test API get User with name='user'`() = runTest {

        client.get("${UserAdminRouting().root}/${user.name}") {
            bearerAuth(adminJWT)
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.OK)
            assertThat(deSerialize<User>(this.bodyAsText())).usingRecursiveComparison(comparisonConfiguration)
                .isEqualTo(user)
        }
    }

    @Test
    fun `Test API get all Users`() = runTest {

        client.get(UserAdminRouting().root) {
            bearerAuth(adminJWT)
        }.apply {

            assertThat(this.status).isEqualTo(HttpStatusCode.OK)
            assertThat(deSerialize<List<User>>(this.bodyAsText())).usingRecursiveComparison(comparisonConfiguration)
                .isEqualTo(listOf(user, admin))
        }
    }

    @Test
    fun `Test API create new user`() = runTest {

        client.post(UserAdminRouting().root) {
            bearerAuth(adminJWT)
            contentType(ContentType.Application.Json)
            setBody(serialize(newUser))
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.Created)
            val userFromRequest = deSerialize<User>(this.bodyAsText())
            assertThat(userFromRequest).usingRecursiveComparison(comparisonConfiguration)
                .isEqualTo(newUser.toUser(userFromRequest.id))
        }
    }

    @Test
    fun `Test API edit existing user`() = runTest {

        client.put("${UserAdminRouting().root}/${user.name}") {
            bearerAuth(adminJWT)
            contentType(ContentType.Application.Json)
            setBody(serialize(updatedUser))
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.NoContent)
            val userInDb = UserService.get(updatedUser.name)
            assertThat(userInDb).usingRecursiveComparison(comparisonConfiguration).isEqualTo(updatedUser.toUser())
        }
    }

    @Test
    fun `Test API delete existing user`() = runTest {

        client.delete("${UserAdminRouting().root}/${user.name}") {
            bearerAuth(adminJWT)
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.NoContent)
            assertThatExceptionOfType(NotFoundException::class.java).isThrownBy { UserService.get(user.name) }
        }
    }

    @Nested
    inner class ErrorTests {

        @Test
        fun `Test API create invalid user`() = runTest {

            client.post(UserAdminRouting().root) {
                bearerAuth(adminJWT)
                contentType(ContentType.Application.Json)
                setBody(serialize(invalidUser))
            }.apply {
                assertThat(this.status).isEqualTo(HttpStatusCode.UnprocessableEntity)
            }
        }

        @Test
        fun `Test API get nonexistent user`() = runTest {

            client.get("${UserAdminRouting().root}/$NON_EXISTENT_USER_NAME") {
                bearerAuth(adminJWT)
            }.apply {
                assertThat(this.status).isEqualTo(HttpStatusCode.NotFound)
            }
        }

        @Test
        fun `Test API get users without ADMIN role`() = runTest {

            client.get(UserAdminRouting().root) {
                bearerAuth(userJWT)
            }.apply {
                assertThat(this.status).isEqualTo(HttpStatusCode.Forbidden)
            }
        }
    }
}