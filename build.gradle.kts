plugins {
    kotlin("jvm") version "2.1.10"
    application //Permite ejecutar con "gradlew run"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0") // Última versión de JUnit 5
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

// Configuración del plugin 'application' (solo si la app tiene un punto de entrada)
application {
    mainClass.set("org.example.MainKt") // Asegúrate de cambiar esto a tu clase principal
}

// Opcional: Habilitar advertencias en la compilación
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xlint", "-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "23"
    }
}