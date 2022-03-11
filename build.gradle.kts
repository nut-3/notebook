val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val openapiVersion: String by project
val h2Version: String by project
val exposedVersion: String by project
val hikariCpVersion: String by project
val flywayVersion: String by project
val loggingVersion: String by project
val assertjVersion: String by project
val mockkVersion: String by project
val junitVersion: String by project
val valiktorVersion: String by project
val bcryptVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.github.notebook"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cio-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$loggingVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("at.favre.lib:bcrypt:$bcryptVersion")

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

    // Valiktor validation
    implementation("org.valiktor:valiktor-core:$valiktorVersion")

    // Tests
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
//    testImplementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
//    testImplementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}