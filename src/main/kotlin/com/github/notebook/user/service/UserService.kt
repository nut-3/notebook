package com.github.notebook.user.service

import com.github.notebook.user.model.*
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object UserService {

    fun getAll(): List<User> = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Users.selectAll().map { Users.toUser(it) }
    }

    fun get(userName: String) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Users.select { (Users.name eq userName) }.mapNotNull { Users.toUser(it) }.singleOrNull()
    }

    fun create(user: NewUser) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val newId = Users.insertAndGetId {
            it[name] = user.name
            it[password] = user.password
            it[active] = user.active
            it[email] = user.email
            it[fullName] = user.fullName
        }
        user.roles.forEach { currentRole ->
            UserRoles.insert {
                it[userId] = newId
                it[role] = currentRole
            }
        }
        return@transaction getById(newId.value)
    }

    fun update(user: EditUser, userName: String) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        if (userName != user.name) throw IllegalArgumentException("Wrong user data")
        val updatedId = Users.select { Users.name eq userName }.single()[Users.id]
        Users.update({ Users.id eq updatedId }) {
            if (user.password != null) it[password] = user.password
            it[active] = user.active
            it[email] = user.email
            it[fullName] = user.fullName
        }
        val oldRoles = UserRoles.select { UserRoles.userId eq updatedId }.mapTo(mutableSetOf()) { it[UserRoles.role] }
        oldRoles.minus(user.roles).forEach {
            UserRoles.deleteWhere { (UserRoles.userId eq updatedId) and (UserRoles.role eq it) }
        }
        user.roles.minus(oldRoles).forEach { currentRole ->
            UserRoles.insert {
                it[userId] = updatedId
                it[role] = currentRole
            }
        }
    }

    fun delete(userName: String) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val num = Users.deleteWhere { Users.name eq userName }
        if (num == 0) throw NotFoundException("User with name=$userName not found")
    }

    private fun getById(userId: Int): User =
        Users.select { (Users.id eq userId) }.map { Users.toUser(it) }.single()
}