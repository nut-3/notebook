package com.github.notebook.service

import com.github.notebook.common.ForbiddenException
import com.github.notebook.model.*
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object UserService {

    fun getAll(): List<User> = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Users.selectAll().map { Users.toUser(it) }
    }

    fun get(userName: String) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Users.select { Users.name eq userName }.mapNotNull { Users.toUser(it) }.singleOrNull()
            ?: throw NotFoundException("User with name=$userName not found")
    }

    fun create(user: NewUser) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val newId = Users.insertAndGetId {
            it[name] = user.name
            it[password] = PasswordService.hashPassword(user.password)
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
        return@transaction get(newId.value)
    }

    fun update(user: EditUser, userName: String) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        if (userName != user.name) throw IllegalArgumentException("Wrong user data")
        val numRows = Users.update({ Users.id eq user.id }) {
            if (user.password != null) it[password] = PasswordService.hashPassword(user.password)
            it[active] = user.active
            it[email] = user.email
            it[fullName] = user.fullName
        }
        if (numRows == 0) throw NotFoundException("User with name=$userName not found")
        val oldRoles = UserRoles.select { UserRoles.userId eq user.id }.mapTo(mutableSetOf()) { it[UserRoles.role] }
        oldRoles.minus(user.roles).forEach {oldRole ->
            UserRoles.deleteWhere { (userId eq user.id) and (role eq oldRole) }
        }
        user.roles.minus(oldRoles).forEach { currentRole ->
            UserRoles.insert {
                it[userId] = user.id
                it[role] = currentRole
            }
        }
    }

    fun delete(userName: String) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val num = Users.deleteWhere { Users.name eq userName }
        if (num == 0) throw NotFoundException("User with name=$userName not found")
    }

    fun getUserStatus(userName: String) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Users.slice(Users.active, Users.password).select { (Users.name eq userName) }.singleOrNull()
            ?: throw ForbiddenException("User with name=$userName not found")
    }

    fun get(userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Users.select { Users.id eq userId }.map { Users.toUser(it) }.singleOrNull()
            ?: throw ForbiddenException("User with id=$userId not found")
    }
}