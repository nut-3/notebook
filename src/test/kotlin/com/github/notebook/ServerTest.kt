package com.github.notebook

import com.github.notebook.authorization.service.JwtService
import com.github.notebook.db.DbConfig
import com.github.notebook.user.model.Role
import io.ktor.client.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll


open class ServerTest {

    companion object {

        private lateinit var unitTestApplication: TestApplication
        lateinit var adminJWT: String
        lateinit var userJWT: String
        lateinit var client: HttpClient

        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        private fun initTests() {
            val testConfig = ApplicationConfig("application.conf")
            JwtService.setConfig(testConfig)
            adminJWT = JwtService.generateJwt("admin", listOf(Role.ADMIN.name, Role.USER.name))
            userJWT = JwtService.generateJwt("user", listOf(Role.USER.name))
            unitTestApplication = TestApplication {
                environment {
                    config = testConfig
                }
            }
            client = unitTestApplication.client.config {
                expectSuccess = false
            }
        }

        @AfterAll
        @JvmStatic
        @Suppress("unused")
        fun stopApp() {
            unitTestApplication.stop()
        }
    }

    @AfterEach
    fun refreshSchema() {
        val dataSource = DbConfig.getHikariDS()
        DbConfig.cleanSchema(dataSource)
        DbConfig.initFlyway(dataSource)
    }

    @Suppress("UNUSED_EXPRESSION")
    fun runTest(
        block: suspend ServerTest.() -> Unit
    ) {
        runBlocking { block() }
    }
}