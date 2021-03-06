@file:UseSerializers(LocalDateTimeAsStringSerializer::class)

package com.github.notebook.model

import com.github.notebook.common.LocalDateTimeAsStringSerializer
import com.github.notebook.service.UserService
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

object Notebooks : IntIdTable(name = "notebooks") {
    val name = varchar("name", 150)
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val created = datetime("create_dt").default(LocalDateTime.now())
    val updated = datetime("update_dt").default(LocalDateTime.now())

    init {
        uniqueIndex(name, userId)
    }
}

@Serializable
data class Notebook(
    val id: Int,
    val name: String,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val owner: String?
)

@Serializable
data class NewNotebook(
    val name: String
) {
    init {
        validate(this) {
            validate(NewNotebook::name).isNotNull().hasSize(min = 3, max = 150)
        }
    }
}

@Serializable
data class EditNotebook(
    val id: Int,
    val name: String
) {
    init {
        validate(this) {
            validate(EditNotebook::id).isNotNull().isPositive()
            validate(EditNotebook::name).isNotNull().hasSize(min = 3, max = 150)
        }
    }
}


fun Notebooks.toNotebook(row: ResultRow, adminMode: Boolean = false): Notebook =
    Notebook(
        id = row[id].value,
        name = row[name],
        created = row[created],
        updated = row[updated],
        owner = if (adminMode) UserService.get(row[userId].value).name else null
    )