package com.github.notebook

import com.github.notebook.db.DbConfig
import com.github.notebook.service.JwtService
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll


open class ServerTest(val rootPath: String) {

    companion object {

        private val testConfig = ApplicationConfig("application.conf")
        private val unitTestApplication = TestApplication {
            environment { config = testConfig }
        }
        val client = unitTestApplication.createClient { expectSuccess = false }
        internal val comparisonConfiguration =
            RecursiveComparisonConfiguration.builder().withIgnoreCollectionOrder(true).build()!!
        lateinit var adminJWT: String
        lateinit var userJWT: String


        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        private fun initTests() {
            JwtService.setConfig(testConfig)
            adminJWT = JwtService.generateJwt(admin)
            userJWT = JwtService.generateJwt(user)
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