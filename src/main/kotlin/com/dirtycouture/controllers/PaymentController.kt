package com.dirtycouture.controllers

import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.tables.CartItems
import com.dirtycouture.db.generated.tables.OrderItems
import com.dirtycouture.db.generated.tables.Orders
import com.dirtycouture.db.generated.tables.ProductVariants
import com.stripe.Stripe
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.jooq.impl.DSL
import java.math.BigDecimal
import com.dirtycouture.db.generated.enums.PaymentStatus
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

enum class OrderStatus(val code: Short) {
    PENDING(0),
    PAID(1),
    SHIPPED(2),
    CANCELLED(3)
}

@Serializable
data class CreateOrderRequest(val shippingAddressId: Long)

@Serializable
data class CreateOrderResponse(val orderId: Long, val stripeUrl: String)

object PaymentController {

    fun configureStripe() {
        val dotenv = io.github.cdimascio.dotenv.dotenv {
            ignoreIfMissing = true
        }
        val key = dotenv["STRIPE_SECRET_KEY"]
        println("✅ Stripe configurado con clave: $key")
        Stripe.apiKey = key
    }

    suspend fun createOrderAndCheckoutSession(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
        val userId = principal.payload.subject?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid JWT subject"))

        val request = try {
            call.receive<CreateOrderRequest>()
        } catch (e: Exception) {
            return call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
        }

        val dsl = DBFactory.dslContext

        val cartRecords = dsl.select(
            CartItems.CART_ITEMS.PRODUCT_VARIANT_ID,
            CartItems.CART_ITEMS.QUANTITY,
            DSL.field("product_variants.price", BigDecimal::class.java).`as`("variant_price")
        )
            .from(CartItems.CART_ITEMS)
            .join(ProductVariants.PRODUCT_VARIANTS)
            .on(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(ProductVariants.PRODUCT_VARIANTS.ID))
            .where(CartItems.CART_ITEMS.USER_ID.eq(userId))
            .fetch()

        if (cartRecords.isEmpty()) {
            return call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Cart is empty"))
        }

        var totalAmount = BigDecimal.ZERO
        val lineItemsParams = mutableListOf<SessionCreateParams.LineItem>()

        cartRecords.forEach { record ->
            val variantId = record.get(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID)
            val quantityBD = record.get(CartItems.CART_ITEMS.QUANTITY) ?: BigDecimal.ONE
            val priceBD = record.get("variant_price", BigDecimal::class.java) ?: BigDecimal.ZERO

            totalAmount += priceBD.multiply(quantityBD)
            val unitAmountCents = priceBD.multiply(BigDecimal(100)).toLong()

            val lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(quantityBD.toLong())
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount(unitAmountCents)
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName("Variant #$variantId")
                                .build()
                        )
                        .build()
                )
                .build()

            lineItemsParams.add(lineItem)
        }

        val createdOrder = dsl.insertInto(Orders.ORDERS)
            .set(Orders.ORDERS.USER_ID, userId)
            .set(Orders.ORDERS.SHIPPING_ADDRESS_ID, request.shippingAddressId)
            .set(Orders.ORDERS.TOTAL, totalAmount.toDouble())
            .set(Orders.ORDERS.STATUS, OrderStatus.PENDING.code)
            .returning(Orders.ORDERS.ID)
            .fetchOne()

        if (createdOrder == null || createdOrder[Orders.ORDERS.ID] == null) {
            return call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Could not create order"))
        }

        val orderId = createdOrder[Orders.ORDERS.ID]!!

        cartRecords.forEach { record ->
            val variantId = record.get(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID)
            val quantityBD = record.get(CartItems.CART_ITEMS.QUANTITY) ?: BigDecimal.ONE
            val priceBD = record.get("variant_price", BigDecimal::class.java) ?: BigDecimal.ZERO

            if (variantId != null) {
                dsl.insertInto(OrderItems.ORDER_ITEMS)
                    .set(OrderItems.ORDER_ITEMS.ORDER_ID, orderId)
                    .set(OrderItems.ORDER_ITEMS.PRODUCT_VARIANT_ID, variantId)
                    .set(OrderItems.ORDER_ITEMS.QUANTITY, quantityBD.toLong())
                    .set(OrderItems.ORDER_ITEMS.PRICE, priceBD.toDouble())
                    .execute()
            }
        }

        val frontendUrl = System.getenv("FRONTEND_URL") ?: "http://localhost:5173"
        val session = Session.create(
            SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("$frontendUrl/acceptPayment?orderId=$orderId")
                .setCancelUrl("$frontendUrl/cancelPayment?orderId=$orderId")
                .also { builder ->
                    lineItemsParams.forEach { builder.addLineItem(it) }
                }
                .build()
        )

        call.respond(
            HttpStatusCode.Created,
            CreateOrderResponse(orderId = orderId, stripeUrl = session.url!!)
        )
    }
}
