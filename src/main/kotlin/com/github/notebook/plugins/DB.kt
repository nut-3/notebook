package com.github.notebook.plugins

import com.github.notebook.db.DbConfig
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.intiDB() {
    log.info("Initiate database")
    val dataSource = DbConfig.getHikariDS()
    Database.connect(dataSource)
    DbConfig.initTcpServer()
    DbConfig.initFlyway(dataSource)
}