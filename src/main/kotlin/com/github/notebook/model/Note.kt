@file:UseSerializers(LocalDateTimeAsStringSerializer::class)

package com.github.notebook.model

import com.github.notebook.common.LocalDateTimeAsStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotNull
import org.valiktor.functions.isPositive
import org.valiktor.validate
import java.time.LocalDateTime

object Notes : IntIdTable(name = "notes") {
    val notebookId = reference("notebook_id", Notebooks.id, onDelete = ReferenceOption.CASCADE)
    val created = datetime("create_dt").default(LocalDateTime.now())
    val updated = datetime("update_dt").default(LocalDateTime.now())
    val subject = varchar("subject", 100)
    val body = text("body").nullable()

    init {
        uniqueIndex(notebookId, subject)
    }
}

@Serializable
data class Note(
    val id: Int,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val subject: String,
    val body: String?
)

@Serializable
data class NewNote(
    val subject: String,
    val body: String?
) {
    init {
        validate(this) {
            validate(NewNote::subject).isNotNull().hasSize(min = 3, max = 100)
        }
    }
}

@Serializable
data class EditNote(
    val id: Int,
    val subject: String,
    val body: String?
) {
    init {
        validate(this) {
            validate(EditNote::id).isNotNull().isPositive()
            validate(EditNote::subject).isNotNull().hasSize(min = 3, max = 100)
        }
    }
}

fun Notes.toNote(row: ResultRow): Note =
    Note(
        id = row[id].value,
        created = row[created],
        updated = row[updated],
        subject = row[subject],
        body = row[body]
    )