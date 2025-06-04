package com.dirtycouture

import com.dirtycouture.controllers.PaymentController
import com.dirtycouture.routes.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.http.content.*
import java.io.File

fun main(args: Array<String>) {
    //Carga .env (credenciales DB) antes de iniciar ktor
    val dotenv = dotenv {
        ignoreIfMissing = true //Por si ya están definidas en produccion (Github y Render credentials)
    }

    //Prioriza la variable de entorno (Render), si no coge de .env o asigna 8080 directamente
    val port = System.getenv("PORT")?.toInt() ?: dotenv["PORT"]?.toInt() ?: 8080

    embeddedServer(Netty, port, module = Application::module).start(wait = true)
}


fun Application.module() {
    // 1. Inicializamos la BD (HikariCP + jOOQ)
    DBFactory.init()


    // 2. Configuramos Stripe
    PaymentController.configureStripe()


    // 3. Variables JWT desde .env
    val jwtSecret = dotenv {
        ignoreIfMissing = true
    }["JWT_SECRET"] ?: throw IllegalStateException("JWT_SECRET no está definido")
    val jwtIssuer = dotenv {
        ignoreIfMissing = true
    }["JWT_ISSUER"] ?: "dirtycouture.io"

    // 4. Instalamos el plugin de Authentication con JWT
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "dirtycouture"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                // Si el token contiene el claim "sub" (userId) y no está expirado, devolvemos un JWTPrincipal
                if (credential.payload.subject != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token inválido o expirado"))
            }
        }
    }

    // 5. Instalamos ContentNegotiation (JSON)
        install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        allowHost("dirtycouture.onrender.com", schemes = listOf("https"))
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowCredentials = true
    }

    // 6. Rutas: separamos públicas y protegidas
    routing {
        // Prueba básica
        get("/ping") {
            call.respondText("pong")
        }

        // Rutas públicas de la API
        authRoutes()       // /auth/register, /auth/login
        productRoutes()    // /api/products, /api/products/{id}/variants
        webhookRoutes()    // /api/payments/webhook (Stripe)

        // Rutas protegidas con JWT
        authenticate("auth-jwt") {
            cartRoutes()         // /api/cart/…
            notificationRoutes() // /api/notifications/…
            orderRoutes()        // /api/orders/…
            paymentRoutes()      // /api/payments/…
            shippingRoutes()
        }

        // Rutas frontend: servir recursos estáticos
        static("/") {
            resources("frontend/dist")
            defaultResource("index.html", "frontend/dist")
        }

        // Catch-all para SPA (opcional, útil si usas Vue Router en modo history)
        get("{...}") {
            val indexFile = File("build/resources/main/frontend/dist/index.html")
            if (indexFile.exists()) {
                call.respondFile(indexFile)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }


    log.info("Servidor iniciado correctamente en modo ${environment.developmentMode}")
}