package com.dirtycouture

import com.dirtycouture.routes.authRoutes
import com.dirtycouture.routes.cartRoutes
import com.dirtycouture.routes.notificationRoutes
import com.dirtycouture.routes.orderRoutes
import com.dirtycouture.routes.paymentRoutes
import com.dirtycouture.routes.productRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.github.cdimascio.dotenv.dotenv

fun main(args: Array<String>) {
    //Carga .env (credenciales DB) antes de iniciar ktor
    val dotenv = dotenv {
        ignoreIfMissing = true //Por si ya están definidas en produccion (Github y Render credentials)
    }

    embeddedServer(Netty, port = dotenv["PORT"]?.toInt() ?: 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    DBFactory.init()

    install(ContentNegotiation) {
        json()
    }

    routing {
        authRoutes()
        cartRoutes()
        notificationRoutes()
        orderRoutes()
        paymentRoutes()
        productRoutes()
    }

    log.info("Servidor iniciado correctamente en modo ${environment.developmentMode}")
}