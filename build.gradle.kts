import java.util.*;

val env = Properties().apply {
    val envFile = file(".env")
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    }
}

val dbUrl = env.getProperty("DB_URL") ?: System.getenv("DB_URL")
val dbUser = env.getProperty("DB_USER") ?: System.getenv("DB_USER")
val dbPswd = env.getProperty("DB_PASS") ?: System.getenv("DB_PASS")

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    application //Allow execute with "gradlew run"
    id("io.ktor.plugin") version "2.3.7"
    id("nu.studer.jooq") version "8.2"
}

group = "com.dirtycouture"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.5.13")

    //Ktor serialization
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    //HikariCP for connection pooling
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("org.jooq:jooq:3.17.6") // JOOQ Core
    implementation("org.jooq:jooq-meta:3.17.6") // Code generation
    implementation("org.jooq:jooq-codegen:3.17.6") // Kotlin extensions

    implementation("org.postgresql:postgresql:42.7.2") // PostgreSQL driver
    jooqGenerator("org.postgresql:postgresql:42.7.2")

    //Tests
    testImplementation("io.ktor:ktor-server-tests:2.3.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.22")
}

tasks.test {
    useJUnitPlatform()

}
kotlin {
    jvmToolchain(17)
}

// Plugin 'application' configuration (only if it has an entry point)
application {
    mainClass.set("com.dirtycouture.MainKt")
}

// Optional: allow compilation warnings
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xlint", "-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "17"
    }
}

//Asegura que el JAR que se genera (lo que ejecuta Render) siempre se llame "app.jar"
tasks.withType<Jar> {
    archiveBaseName.set("app")
    archiveVersion.set("") // opcional: evita nombres como app-1.0.jar
}


//DB variables saved on GitHub Secrets, is this necessary?
jooq {
    version.set("3.17.6")
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPswd
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                        isDaos = true
                        isPojos = true
                    }
                    target.apply {
                        packageName = "com.dirtycouture.db.generated"
                        directory = "supabase/generated/db"
                    }
                }
            }
        }
    }
}

sourceSets["main"].kotlin.srcDir("supabase/generated/db")