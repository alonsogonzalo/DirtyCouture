package com.dirtycouture.routes


import io.ktor.server.routing.*
import com.dirtycouture.controllers.PaymentController
import io.ktor.server.application.*

fun Route.paymentRoutes() {
    route("/api/payments") {
        post("{idOrder}"){PaymentController.processOrderToPay(call)}
        post("/webhook"){ PaymentController.webhook(call)}

    }
}