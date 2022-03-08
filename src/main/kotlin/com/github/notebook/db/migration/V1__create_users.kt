package com.github.notebook.db.migration

import com.github.notebook.user.model.Role
import com.github.notebook.user.model.UserRoles
import com.github.notebook.user.model.Users
import com.github.notebook.user.service.PasswordService
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class V1__create_users : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(Users, UserRoles)

            val regularId = Users.insert {
                it[name] = "user"
                it[password] = PasswordService.hashPassword("password")
                it[active] = true
                it[email] = "user@test.test"
                it[fullName] = "User User"
            }[Users.id]

            val adminId = Users.insert {
                it[name] = "admin"
                it[password] = PasswordService.hashPassword("admin")
                it[active] = true
                it[email] = "admin@test.test"
                it[fullName] = "Admin Admin"
            }[Users.id]

            UserRoles.insert {
                it[userId] = regularId
                it[role] = Role.USER
            }

            UserRoles.insert {
                it[userId] = adminId
                it[role] = Role.USER
            }

            UserRoles.insert {
                it[userId] = adminId
                it[role] = Role.ADMIN
            }
        }
    }
}