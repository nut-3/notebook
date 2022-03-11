package com.github.notebook.user.web

import com.github.notebook.ServerTest
import com.github.notebook.common.JsonMapper
import com.github.notebook.common.deSerialize
import com.github.notebook.common.serialize
import com.github.notebook.user.*
import com.github.notebook.user.model.User
import com.github.notebook.user.service.UserService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import kotlinx.serialization.decodeFromString
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserAdminRoutingKtTest : ServerTest() {


    @Test
    fun `Test API get User with name='user'`(): Unit = runTest {

        client.get("${UserAdminRouting().root}/${user.name}") {
            bearerAuth(adminJWT)
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.OK)
            assertThat(deSerialize<User>(this.bodyAsText())).usingRecursiveComparison().isEqualTo(user)
        }
    }

    @Test
    fun `Test API get all Users`(): Unit = runTest {

        client.get(UserAdminRouting().root) {
            bearerAuth(adminJWT)
        }.apply {

            assertThat(this.status).isEqualTo(HttpStatusCode.OK)
            assertThat(JsonMapper.decodeFromString<List<User>>(this.bodyAsText())).usingRecursiveComparison()
                .isEqualTo(listOf(user, admin))
        }
    }

    @Test
    fun `Test API create new user`(): Unit = runTest {

        client.post(UserAdminRouting().root) {
            bearerAuth(adminJWT)
            contentType(ContentType.Application.Json)
            setBody(serialize(newUser))
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.Created)
            val userFromRequest = deSerialize<User>(this.bodyAsText())
            assertThat(userFromRequest).usingRecursiveComparison().isEqualTo(newUser.toUser(userFromRequest.id))
        }
    }

    @Test
    fun `Test API edit existing user`(): Unit = runTest {

        client.put("${UserAdminRouting().root}/${user.name}") {
            bearerAuth(adminJWT)
            contentType(ContentType.Application.Json)
            setBody(serialize(updatedUser))
        }.apply {
            assertThat(this.status).isEqualTo(HttpStatusCode.NoContent)
            val userInDb = UserService.get(updatedUser.name)
            assertThat(userInDb).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoreCollectionOrder(true).build()
            ).isEqualTo(updatedUser.toUser())
        }
    }

    @Test
    fun `Test API delete existing user`(): Unit = runTest {

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
        fun `Test API create invalid user`(): Unit = runTest {

            client.post(UserAdminRouting().root) {
                bearerAuth(adminJWT)
                contentType(ContentType.Application.Json)
                setBody(serialize(invalidUser))
            }.apply {
                assertThat(this.status).isEqualTo(HttpStatusCode.UnprocessableEntity)
            }
        }

        @Test
        fun `Test API get nonexistent user`(): Unit = runTest {

            client.get("${UserAdminRouting().root}/$nonExistentUserName") {
                bearerAuth(adminJWT)
            }.apply {
                assertThat(this.status).isEqualTo(HttpStatusCode.NotFound)
            }
        }

        @Test
        fun `Test API get users without ADMIN role`(): Unit = runTest {

            client.get(UserAdminRouting().root) {
                bearerAuth(userJWT)
            }.apply {
                assertThat(this.status).isEqualTo(HttpStatusCode.Forbidden)
            }
        }
    }
}