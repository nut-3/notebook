[![Codacy Badge](https://app.codacy.com/project/badge/Grade/3dd5e912a3df45329ac12ef789ccaed0)](https://www.codacy.com/gh/nut-3/notebook/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nut-3/notebook&amp;utm_campaign=Badge_Grade) [![Build Status](https://app.travis-ci.com/nut-3/notebook.svg?branch=main)](https://app.travis-ci.com/nut-3/notebook)

# Simple RESTFul web service in Kotlin
Kotlin 1.6.10 and Ktor 2.0.0-beta-1

## Getting Started
 1. Clone the repo.
 2. In the root directory execute `./gradlew run`
 3. By default, the server will start on port 8080
## Libraries
 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - CIO - Coroutine-based I/O as engine
 - [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization) - JSON serialization/deserialization
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework
 - [Valiktor](https://github.com/valiktor/valiktor) - Kotlin validation framework
 - [H2](https://github.com/h2database/h2database) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Flyway](https://flywaydb.org/) - Database migrations
 - [JUnit 5](https://junit.org/junit5/), [AssertJ](http://joel-costigliola.github.io/assertj/) and Ktor Server Test for testing

Inspired by [kotlin-ktor-exposed-starter]( https://github.com/raharrison/kotlin-ktor-exposed-starter)