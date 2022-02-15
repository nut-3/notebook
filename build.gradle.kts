val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val openapiVersion: String by project
val h2Version: String by project
val exposedVersion: String by project
val hikariCpVersion: String by project
val flywayVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.github.notebook"
version = "0.0.1"
application {
    mainClass.set("com.github.notebook.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")

    // https://mvnrepository.com/artifact/com.h2database/h2
    implementation("com.h2database:h2:$h2Version")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    // HikariCP
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")

    // Flyway
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    // Tests
//    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    //Swagger
//    implementation("org.springdoc:springdoc-openapi-data-rest:$openapiVersion")
//    implementation("org.springdoc:springdoc-openapi-ui:$openapiVersion")
//    implementation("org.springdoc:springdoc-openapi-kotlin:$openapiVersion")
}