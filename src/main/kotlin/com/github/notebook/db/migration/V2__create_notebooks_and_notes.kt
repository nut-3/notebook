package com.github.notebook.db.migration

import com.github.notebook.model.Notebooks
import com.github.notebook.model.Notes
import com.github.notebook.model.Users
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class V2__create_notebooks_and_notes : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(Notebooks, Notes)

            val userNotebookId1 = Notebooks.insert {
                it[name] = "User Notebook 01"
                it[userId] = Users.slice(Users.id).select(Users.name eq "user")
            }[Notebooks.id]

            Notebooks.insert {
                it[name] = "User Notebook 02"
                it[userId] = Users.slice(Users.id).select(Users.name eq "user")
            }[Notebooks.id]

            val adminNotebookId = Notebooks.insert {
                it[name] = "Admin Notebook 01"
                it[userId] = Users.slice(Users.id).select(Users.name eq "admin")
            }[Notebooks.id]

            Notes.insert {
                it[notebookId] = userNotebookId1
                it[subject] = "User note subject1"
                it[body] = """
                    Test Body for the first user note
                    Blah
                    Blah
                    Blah
                """.trimIndent()
            }

            Notes.insert {
                it[notebookId] = userNotebookId1
                it[subject] = "User note subject2"
                it[body] = """
                    Test Body for the second user note
                    Blah2
                    Blah2
                    Blah2
                """.trimIndent()
            }

            Notes.insert {
                it[notebookId] = adminNotebookId
                it[subject] = "Admin note1"
                it[body] = "Test Body for admin note"
            }
        }
    }
}