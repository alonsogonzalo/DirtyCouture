package com.dirtycouture.routes

import com.dirtycouture.controllers.WebhookController
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Route.webhookRoutes() {
    route("/stripe") {
        post("/webhook") {
            WebhookController.handleWebhook(call)
        }
    }
}