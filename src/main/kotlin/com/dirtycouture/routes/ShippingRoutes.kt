package com.dirtycouture.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import com.dirtycouture.controllers.ShippingController
import io.ktor.server.auth.*

fun Route.shippingRoutes() {
    route("/api/shipping") {
        // GET /api/shipping/{userId}
        get("/{userId}") {
            ShippingController.getUserAddresses(call)
        }

        // POST /api/shipping  (requiere JWT)
        authenticate("auth-jwt") {
            post {
                ShippingController.addAddress(call)
            }
        }
    }
}
