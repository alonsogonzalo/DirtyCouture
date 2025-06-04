// src/main/kotlin/com/dirtycouture/routes/PaymentRoutes.kt

package com.dirtycouture.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.dirtycouture.controllers.PaymentController
import io.ktor.server.auth.*
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.org.apache.http.client.methods.RequestBuilder

/**
 * Registrar las rutas de pago.
 * - La ruta de creación de checkout (POST) requiere JWT válido.
 * - El webhook (no se muestra aquí) NO requeriría JWT.
 */
fun Route.paymentRoutes() {
    route("/api/payment") {
        authenticate("auth-jwt") {
            // POST /api/payment/create-checkout-session
            post("/create-checkout-session") {
                PaymentController.createOrderAndCheckoutSession(call)
            }
        }
        post("/webhook") {
            PaymentController.handleStripeWebhook(call)
        }
    }
}
