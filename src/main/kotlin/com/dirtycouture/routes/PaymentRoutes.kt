package com.dirtycouture.routes


import io.ktor.server.routing.*
import controllers.PaymentController

fun Route.paymentRoutes() {
    route("/api/payments") {
        post("{idOrder}", PaymentController::processOrderToPay)
        post("/webhook", PaymentController::webhook)

    }
}