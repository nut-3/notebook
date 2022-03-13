package com.github.notebook.service

import com.github.notebook.model.EditNotebook
import com.github.notebook.model.NewNotebook
import com.github.notebook.model.Notebooks
import com.github.notebook.model.toNotebook
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object NotebookService {

    fun getAllByUserId(userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Notebooks.select { Notebooks.userId eq userId }.mapNotNull { Notebooks.toNotebook(it) }
    }

    fun getByIdAndUserId(notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Notebooks.select { Notebooks.userId eq userId and (Notebooks.id eq notebookId) }.map { Notebooks.toNotebook(it) }
            .singleOrNull() ?: throw NotFoundException("Notebook with id=$notebookId and userId=$userId not found")
    }

    fun updateByIdAndUserId(notebook: EditNotebook, notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        if (notebook.id != notebookId) throw IllegalArgumentException("Wrong notebook data")
        val updatedRows = Notebooks.update({Notebooks.id eq notebookId}) {
            it[name] = notebook.name
            it[updated] = LocalDateTime.now()
        }
        if (updatedRows == 0) throw NotFoundException("Notebook with id=$id and userId=$userId not found")
    }

    fun createByIdAndUserId(notebook: NewNotebook, principalId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val newId = Notebooks.insertAndGetId {
            it[name] = notebook.name
            it[userId] = principalId
        }
        return@transaction get(newId.value)
    }

    fun deleteByIdAndUserId(notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val updatedRows = Notebooks.deleteWhere { Notebooks.id eq notebookId }
        if (updatedRows == 0) throw NotFoundException("Notebook with id=$notebookId and userId=$userId not found")
    }

    fun get(notebookId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        Notebooks.select { Notebooks.id eq notebookId }.map { Notebooks.toNotebook(it) }
            .singleOrNull() ?: throw NotFoundException("Notebook with id=$notebookId not found")
    }
}