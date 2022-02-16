package com.github.notebook.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.Location
import org.h2.tools.Server
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object DbConfig {
    private val log = KotlinLogging.logger {}

    fun intiDB() {
        log.info { "Initiate database" }
        val dataSource = getHikariDS()
        Database.connect(dataSource)
        initTcpServer()
        initFlyway(dataSource)
    }

    private fun initTcpServer() {
        log.info { "Start H2 TCP server" }
        Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start()
    }

    private fun getHikariDS() = HikariDataSource(HikariConfig("/hikari.properties"))

    private fun initFlyway(dataSource: DataSource) {
        log.info { "Initiate flyway migration" }
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations(Location("classpath:**/db/migration"))
            .load()
        try {
            flyway.info()
            flyway.migrate()
        } catch (e: FlywayException) {
            log.error { "Exception running flyway migration" }
            throw e
        }
    }
}