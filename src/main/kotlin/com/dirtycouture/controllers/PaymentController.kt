package com.dirtycouture.controllers

import com.dirtycouture.models.StripeItemDTO
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*

import com.stripe.Stripe
import com.stripe.model.Review
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams

object PaymentController {

    fun configureStripe() {
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY")
    }

    suspend fun createCheckoutSession(call: ApplicationCall) {
        val domain = System.getenv("FRONTEND_URL")

        val items = try {
            call.receive<List<StripeItemDTO>>()
        } catch (e: Exception) {
            return call.respond(HttpStatusCode.BadRequest, "Invalid request body")
        }

        if (items.isEmpty()) {
            return call.respond(HttpStatusCode.BadRequest, "Empty cart")
        }

        val sessionBuilder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("$domain/acceptPayment")
            .setCancelUrl("$domain/cancelPayment")

        items.forEach { item ->
            val lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(item.quantity)
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount((item.price * 100).toLong()) //Stripe usa céntimos, hay que convertirlo
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(item.name)
                                .build()
                        )
                        .build()
                )
                .build()

            sessionBuilder.addLineItem(lineItem)
        }

        val session = Session.create(sessionBuilder.build())
        call.respond(mapOf("url" to session.url))
    }
}