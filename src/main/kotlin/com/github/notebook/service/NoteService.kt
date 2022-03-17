package com.github.notebook.service

import com.github.notebook.model.*
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object NoteService {

    fun getAll(notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        (Notes innerJoin Notebooks).slice(Notes.fields + Notebooks.userId)
            .select { Notes.notebookId eq notebookId and (Notebooks.userId eq userId) }
            .mapNotNull { Notes.toNote(it) }
    }

    fun get(noteId: Int, notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        (Notes innerJoin Notebooks).slice(Notes.fields + Notebooks.userId)
            .select { Notes.id eq noteId and (Notes.notebookId eq notebookId) and (Notebooks.userId eq userId) }
            .map { Notes.toNote(it) }
            .singleOrNull()
            ?: throw NotFoundException("Note with id=$noteId, notebookId=$notebookId and userId=$userId not found")
    }

    fun create(newNote: NewNote, notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val notebookIdInDb = Notebooks.slice(Notebooks.id)
            .select { Notebooks.id eq notebookId and (Notebooks.userId eq userId) }.map { it[Notebooks.id].value }
            .single()
        val newNoteId = Notes.insertAndGetId {
            it[this.notebookId] = notebookIdInDb
            it[subject] = newNote.subject
            it[body] = newNote.body
        }
        return@transaction Notes.select { Notes.id eq newNoteId }.map { Notes.toNote(it) }.single()
    }

    fun update(editNote: EditNote, noteId: Int, notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        if (editNote.id != noteId) throw IllegalArgumentException("Wrong note data")
        val notebookIdInDb = Notebooks.slice(Notebooks.id)
            .select { Notebooks.id eq notebookId and (Notebooks.userId eq userId) }.map { it[Notebooks.id].value }
            .singleOrNull()
        val updatedRows = Notes.update({ Notes.id eq noteId and (Notes.notebookId eq notebookIdInDb) }) {
            it[subject] = editNote.subject
            it[body] = editNote.body
            it[updated] = LocalDateTime.now()
        }
        if (updatedRows == 0) throw NotFoundException("Note with id=$id and notebookId=$notebookId and userId=$userId not found")
    }

    fun delete(noteId: Int, notebookId: Int, userId: Int) = transaction {
        addLogger(Slf4jSqlDebugLogger)
        val notebookIdInDb = Notebooks.slice(Notebooks.id)
            .select { Notebooks.id eq notebookId and (Notebooks.userId eq userId) }.map { it[Notebooks.id].value }
            .singleOrNull()
        val deletedRows = Notes.deleteWhere { Notes.id eq noteId and (Notes.notebookId eq notebookIdInDb) }
        if (deletedRows == 0) throw NotFoundException("Note with id=$id and notebookId=$notebookId and userId=$userId not found")
    }
}