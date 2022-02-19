package com.github.notebook

import com.github.notebook.common.JsonMapper
import com.github.notebook.db.DbConfig
import com.github.notebook.plugins.*
import com.github.notebook.user.model.Users
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import kotlinx.serialization.decodeFromString
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import java.util.concurrent.TimeUnit


//https://github.com/raharrison/kotlin-ktor-exposed-starter/blob/master/src/test/kotlin/common/ServerTest.kt
open class ServerTest {

    protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
        return JsonMapper.decodeFromString(this.asString())
    }

    companion object {

        private var serverStarted = false

        private lateinit var server: ApplicationEngine

        @BeforeAll
        @JvmStatic
        fun startServer() {
            if (!serverStarted) {
                server = embeddedServer(Netty, port = 8081, host = "localhost") {
                    DbConfig.intiDB()
                    configureRouting()
                    configureSerialization()
                    configureMonitoring()
                    configureHTTP()
                    configureSecurity()
                }
                server.start()
                serverStarted = true

                RestAssured.baseURI = "http://localhost"
                RestAssured.port = 8081
                Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0, TimeUnit.SECONDS) })
            }
        }
    }

    @AfterEach
    fun deleteUsers() {
        transaction {
            Users.deleteWhere { (Users.id.notInList(listOf(1, 2))) }
        }
    }
}