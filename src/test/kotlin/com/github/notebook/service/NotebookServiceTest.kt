package com.github.notebook.service

import com.github.notebook.db.DbConfig
import com.github.notebook.user
import com.github.notebook.userNotebook1
import com.github.notebook.userNotebook2
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.Test

internal class NotebookServiceTest {

    init {
        val dataSource = DbConfig.getHikariDS()
        Database.connect(dataSource)
        DbConfig.initFlyway(dataSource)
    }

    @Test
    fun getAll() {
        val notebooks = NotebookService.getAll(user.id)
        assertThat(notebooks).usingRecursiveComparison(
            RecursiveComparisonConfiguration.builder().withIgnoreCollectionOrder(true).build()
        )
            .ignoringFields("created", "updated", "owner")
            .isEqualTo(listOf(userNotebook1, userNotebook2))
    }
}