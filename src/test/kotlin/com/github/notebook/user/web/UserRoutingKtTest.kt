package com.github.notebook.user.web

import com.github.notebook.ServerTest
import com.github.notebook.user.model.EditUser
import com.github.notebook.user.model.NewUser
import com.github.notebook.user.model.Role
import com.github.notebook.user.model.User
import com.github.notebook.user.service.UserService
import io.ktor.http.*
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import org.junit.jupiter.api.Test

class UserRoutingKtTest : ServerTest() {

    private val user = User(1, "user", true, "user@test.test", "User User", setOf(Role.USER))
    private val admin = User(2, "admin", true, "admin@test.test", "Admin Admin", setOf(Role.USER, Role.ADMIN))
    private val newUser = NewUser("new", "new_password", true, "new_user@test.test", "New User", setOf(Role.USER))
    private val updatedUser = user.toEditUser(email = "user1@test.test", roles = setOf(Role.ADMIN, Role.USER))
    private val deletedUser =
        NewUser("deleted", "deleted", true, "deleted@test.test", "Deleted Deleted", setOf(Role.USER))

    @Test
    fun `Test API get User with name='user'`() {
        val response = get("$ROUTING_ROOT/${user.name}")
            .then()
            .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatusCode.OK.value)
        assertThat(response.to<User>()).usingRecursiveComparison().isEqualTo(user)
    }

    @Test
    fun `Test API get all Users`() {
        val response = get(ROUTING_ROOT)
            .then()
            .extract()
        assertThat(response.statusCode()).isEqualTo(HttpStatusCode.OK.value)
        assertThat(response.to<List<User>>()).usingRecursiveComparison().isEqualTo(listOf(user, admin))
    }

    @Test
    fun `Test API create new user`() {
        val response = given()
            .contentType(ContentType.JSON)
            .body(Json.encodeToString(newUser))
            .`when`()
            .post(ROUTING_ROOT)
            .then()
            .extract()
        assertThat(response.statusCode()).isEqualTo(HttpStatusCode.Created.value)
        val userFromRequest = response.to<User>()
        assertThat(userFromRequest).usingRecursiveComparison().isEqualTo(newUser.toUser(userFromRequest.id))
    }

    @Test
    fun `Test API edit existing user`() {
        val response = given()
            .contentType(ContentType.JSON)
            .body(Json.encodeToString(updatedUser))
            .`when`()
            .put("$ROUTING_ROOT/${user.name}")
            .then()
            .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatusCode.NoContent.value)
        val userInDb = UserService.get(updatedUser.name)
        assertThat(userInDb).usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
            .withIgnoreCollectionOrder(true).build()).isEqualTo(updatedUser.toUser())
    }

    @Test
    fun `Test API delete existing user`() {
        val userToDelete = UserService.create(deletedUser)
        val response = delete("$ROUTING_ROOT/${userToDelete.name}")
            .then()
            .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatusCode.NoContent.value)
        val userInDb = UserService.get(userToDelete.name)
        assertThat(userInDb).isNull()
    }

    private fun User.toEditUser(
        name: String = this.name,
        password: String? = null,
        active: Boolean = this.active,
        email: String = this.email,
        fullName: String? = this.fullName,
        roles: Set<Role> = this.roles
    ) = EditUser(
        this.id,
        name,
        password,
        active,
        email,
        fullName,
        roles
    )

    private fun EditUser.toUser() = User(
        this.id,
        this.name,
        this.active,
        this.email,
        this.fullName,
        this.roles
    )

    private fun NewUser.toUser(id: Int) = User(
        id,
        this.name,
        this.active,
        this.email,
        this.fullName,
        this.roles
    )
}