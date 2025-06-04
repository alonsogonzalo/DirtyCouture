package com.dirtycouture.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.dirtycouture.controllers.ShippingController
import io.ktor.server.auth.*

fun Route.shippingRoutes() {
    route("/api/shipping") {
        // GET /api/shipping/{userId}
        get("/{userId}") {
            ShippingController.getUserAddresses(call)
        }

        authenticate("auth-jwt") {
            // POST /api/shipping
            post {
                ShippingController.addAddress(call)
            }
            // DELETE /api/shipping/{addressId}
            delete("{addressId}") {
                ShippingController.deleteAddress(call)
            }
        }
    }
}
