package com.dirtycouture

import com.dirtycouture.controllers.PaymentController
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
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

fun main(args: Array<String>) {
    //Carga .env (credenciales DB) antes de iniciar ktor
    val dotenv = dotenv {
        ignoreIfMissing = true //Por si ya están definidas en produccion (Github y Render credentials)
    }

    embeddedServer(Netty,
        port = dotenv["PORT"]?.toInt() ?: 8080,
        module = Application::module).start(wait = true)
}


fun Application.module() {
    // 1. Inicializamos la BD (HikariCP + jOOQ)
    DBFactory.init()

    // 2. Configuramos Stripe
    // PaymentController.configureStripe()

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
        anyHost() // ⚠️ En producción usa .host("tudominio.com", schemes = listOf("https"))
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
    }


    // 6. Rutas: separamos públicas y protegidas
    routing {
        // Rutas públicas (no requieren token)
        authRoutes()    // /auth/register y /auth/login
        productRoutes() // /api/products, /api/products/{id}/variants

        // Rutas protegidas (requieren JWT válido)
        authenticate("auth-jwt") {
            cartRoutes()         // /api/cart/...
            orderRoutes()        // /api/orders/...
            paymentRoutes()      // /api/payments/...
            notificationRoutes() // /api/notifications/...
        }

        // Webhook de Stripe no requiere JWT
       // webhookRoutes()
    }

    log.info("Servidor iniciado correctamente en modo ${environment.developmentMode}")
}