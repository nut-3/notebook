package com.github.notebook

import com.github.notebook.db.DbConfig
import com.github.notebook.model.Role
import com.github.notebook.service.JwtService
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll


open class ServerTest {

    companion object {

        private val testConfig = ApplicationConfig("application.conf")
        private val unitTestApplication = TestApplication {
            environment { config = testConfig }
        }
        val client = unitTestApplication.createClient { expectSuccess = false }
        lateinit var adminJWT: String
        lateinit var userJWT: String


        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        private fun initTests() {
            JwtService.setConfig(testConfig)
            adminJWT = JwtService.generateJwt("admin", listOf(Role.ADMIN.name, Role.USER.name))
            userJWT = JwtService.generateJwt("user", listOf(Role.USER.name))
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