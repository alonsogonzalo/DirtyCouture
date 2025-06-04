package com.dirtycouture.controllers

import com.stripe.model.Event
import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*

object WebhookController {
    suspend fun handleWebhook(call: ApplicationCall) {
        val payload = call.receiveText()
        val sigHeader = call.request.header("Stripe-Signature")
        val endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET")

        val event: Event = try {
            Webhook.constructEvent(payload, sigHeader, endpointSecret)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Webhook error: ${e.message}")
            return
        }

        when (event.type) {
            "checkout.session.completed" -> {
                val session = event.dataObjectDeserializer
                    .`object`
                    .orElse(null) as? Session

                session?.let {
                    val customerEmail = it.customerDetails?.email
                    val amountTotal = it. amountTotal
                    val sessionId = it.id

                    println("Payment completed by $customerEmail for $amountTotal cents. Session ID: $sessionId")

                    //TODO: Guarda el pedido, actualiza stock, etc.
                }
            }
            else -> {
                println("Received event of unknown type: ${event.type}")
            }
        }

        call.respond(HttpStatusCode.OK)
    }
}