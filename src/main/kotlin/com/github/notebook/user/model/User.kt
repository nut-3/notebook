package com.github.notebook.user.model

import com.github.notebook.user.model.UserRoles.role
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object Users : IntIdTable("users") {
    val name = varchar("name", 50).uniqueIndex()
    val password = varchar("password", 150)
    val active = bool("active").default(true)
    val email = varchar("email", 100).uniqueIndex()
    val fullName = varchar("full_name", 200).nullable()
}

object UserRoles : Table("user_roles") {
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val role = enumerationByName("role", 10, Role::class)
}

@kotlinx.serialization.Serializable
data class User(
    val id: Int,
    val name: String,
    val active: Boolean,
    val email: String,
    val fullName: String?,
    val roles: Set<Role> = emptySet()
)

@kotlinx.serialization.Serializable
data class NewUser(
    val name: String,
    val password: String,
    val active: Boolean,
    val email: String,
    val fullName: String?,
    val roles: Set<Role> = emptySet()
)

@kotlinx.serialization.Serializable
data class EditUser(
    val id: Int,
    val name: String,
    val password: String?,
    val active: Boolean,
    val email: String,
    val fullName: String?,
    val roles: Set<Role> = emptySet()
)

enum class Role { USER, ADMIN }


fun Users.toUser(row: ResultRow): User =
    User(
        id = row[id].value,
        name = row[name],
        active = row[active],
        email = row[email],
        fullName = row[fullName],
        roles = UserRoles.select(UserRoles.userId eq row[id]).map { it[role] }.toSet()
    )