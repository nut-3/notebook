package com.github.notebook.user.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Users : IntIdTable("users") {
    val name = varchar("name", 50)
    val password = varchar("password", 150)
    val active = bool("active").default(true)
    val email = varchar("email", 100)
    val fullName = varchar("full_name", 200).nullable()
}

object UserRoles : Table("user_roles") {
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val role = Users.enumerationByName("role", 10, Role::class)
}

@kotlinx.serialization.Serializable
data class User(
    val id: Int,
    val name: String,
    val password: String,
    val roles: Set<Role> = emptySet(),
    val active: Boolean,
    val email: String,
    val fullName: String?
)

enum class Role { USER, ADMIN }
