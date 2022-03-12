package com.github.notebook.model

import com.github.notebook.model.UserRoles.role
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isPositive
import org.valiktor.validate

object Users : IntIdTable("users") {
    val name = varchar("name", 50).uniqueIndex()
    val password = varchar("password", 255)
    val active = bool("active").default(true)
    val email = varchar("email", 100).uniqueIndex()
    val fullName = varchar("full_name", 200).nullable()
}

object UserRoles : Table("user_roles") {
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val role = enumerationByName("role", 10, Role::class)

    init {
        uniqueIndex(userId, role)
    }
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
) {
    init {
        validate(this) {
            validate(NewUser::name).hasSize(min = 3, max = 50)
            validate(NewUser::password).hasSize(min = 6, max = 255)
            validate(NewUser::email).isEmail()
            validate(NewUser::roles).isNotEmpty()
        }
    }
}

@kotlinx.serialization.Serializable
data class EditUser(
    val id: Int,
    val name: String,
    val password: String?,
    val active: Boolean,
    val email: String,
    val fullName: String?,
    val roles: Set<Role> = emptySet()
) {
    init {
        validate(this) {
            validate(EditUser::id).isPositive()
            validate(EditUser::name).hasSize(min = 3, max = 50)
            if (password != null) validate(EditUser::password).hasSize(min = 6, max = 255)
            validate(EditUser::email).isEmail()
            validate(EditUser::roles).isNotEmpty()
        }
    }
}

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