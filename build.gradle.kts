plugins {
    kotlin("jvm") version "1.9.22"
    application //Allow execute with "gradlew run"
	id("io.ktor.plugin") version "2.3.7"
	id("nu.studer.jooq") version "8.2"
}

group = "com.dirtycouture"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
	implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
	implementation("ch.qos.logback:logback-classic:1.4.14")

    //Ktor serialization
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

	//HikariCP for connection pooling
	implementation("com.zaxxer.HikariCP:5.0.1")

    implementation("org.jooq:jooq:3.17.6") // JOOQ Core
    implementation("org.jooq:jooq-meta:3.17.6") // Code generation
    implementation("org.jooq:jooq-codegen:3.17.6") // Kotlin extensions

    implementation("org.postgresql:postgresql:42.6.0") // PostgreSQL driver

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

//DB variables saved on GitHub Secrets, is this necessary?
tasks.withType<nu.studer.gradle.jooq.JooqGenerate>().configureEach {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/postgres"
            user = System.getenv("DB_USER") ?: "postgres"
            password = System.getenv("DB_PASSWORD") ?: "password"
        }
        generator {
            database {
                inputSchema = "public"
            }
            generate {
                daos = true
                pojos = true
            }
        }
    }
}
