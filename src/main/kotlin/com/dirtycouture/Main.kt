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
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*

fun main(args: Array<String>) {
    //Carga .env (credenciales DB) antes de iniciar ktor
    val dotenv = dotenv {
        ignoreIfMissing = true //Por si ya están definidas en produccion (Github y Render credentials)
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    DBFactory.init()

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/ping") {
            call.respondText("pong")
        }
        staticResources("/", "frontend.dist")

        // Sirve index.html para rutas que no se encuentren (frontend SPA routing)
        get("{...}") {
            call.respondText(
                this::class.java.classLoader.getResource("frontend.dist/index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        authRoutes()
        cartRoutes()
        notificationRoutes()
        orderRoutes()
        paymentRoutes()
        productRoutes()
    }

    log.info("Servidor iniciado correctamente en modo ${environment.developmentMode}")
}