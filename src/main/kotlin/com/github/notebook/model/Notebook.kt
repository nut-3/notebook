package com.github.notebook.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Notebooks: IntIdTable(name = "notebooks") {
    val name = varchar("name", 50)
    val userId = reference("user_id", id, onDelete = ReferenceOption.CASCADE)
}