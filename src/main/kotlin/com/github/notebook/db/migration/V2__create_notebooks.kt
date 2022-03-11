package com.github.notebook.db.migration

import com.github.notebook.model.Notebooks
import com.github.notebook.model.Users
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class V2__create_notebooks : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(Notebooks)

            Notebooks.insert {
                it[name] = "User Notebook 01"
                it[userId] = Users.slice(Users.id).select(Users.name eq "user")
            }

            Notebooks.insert {
                it[name] = "Admin Notebook 01"
                it[userId] = Users.slice(Users.id).select(Users.name eq "admin")
            }
        }
    }
}