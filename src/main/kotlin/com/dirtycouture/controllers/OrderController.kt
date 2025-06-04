package com.dirtycouture.controllers

import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.tables.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

object OrderController {

    @Serializable
    data class OrderItemResponse(
        val variantId: Long,
        val quantity: Long,
        val price: Double,
        val size: String,
        val color: String,
        val imageUrl: String,
        val productName: String
    )

    @Serializable
    data class OrderResponse(
        val id: Long,
        val total: Double,
        val status: Short,
        val createdAt: String,
        val paymentStatus: String,
        val items: List<OrderItemResponse>
    )

    suspend fun getActuallyOrder(call: ApplicationCall) {
        val cardId = call.parameters["cardId"]?.toLong()
        val userId = call.parameters["userId"]?.toLong()
        if (cardId == null) {
            call.respond(HttpStatusCode.BadRequest, "Empty fields")
            return
        }
        val cartFind = DBFactory.dslContext.selectFrom(Orders.ORDERS)
            .where(Orders.ORDERS.USER_ID.eq(userId))
            .fetchInto(Orders::class.java)
        if (cartFind.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "No orders found")
            return
        }
        call.respond(cartFind)
    }

    suspend fun getAllOrderByIdUser(call: ApplicationCall) {
        val userId = call.parameters["userId"]?.toLong()
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Empty fields")
            return
        }
        val allUserOrders = DBFactory.dslContext.selectFrom(Orders.ORDERS)
            .where(CartItems.CART_ITEMS.USER_ID.eq(userId))
            .fetchInto(Orders::class.java)

        if (allUserOrders.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "No orders found")
            return
        } else {
            call.respond(allUserOrders)
        }
    }

    suspend fun getOrdersWithItems(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized)

        val userId = principal.payload.subject?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.Unauthorized)

        val dsl = DBFactory.dslContext

        try {
            val orders = dsl.selectFrom(Orders.ORDERS)
                .where(Orders.ORDERS.USER_ID.eq(userId))
                .orderBy(Orders.ORDERS.CREATED_AT.desc().nullsLast())
                .fetch()

            val orderIds = orders.mapNotNull { it.id }

            val allItems = dsl.select(
                OrderItems.ORDER_ITEMS.ORDER_ID,
                OrderItems.ORDER_ITEMS.PRODUCT_VARIANT_ID,
                OrderItems.ORDER_ITEMS.QUANTITY,
                OrderItems.ORDER_ITEMS.PRICE,
                ProductVariants.PRODUCT_VARIANTS.SIZE,
                ProductVariants.PRODUCT_VARIANTS.COLOR,
                ProductVariants.PRODUCT_VARIANTS.IMAGE_URL,
                Products.PRODUCTS.NAME
            )
                .from(OrderItems.ORDER_ITEMS)
                .join(ProductVariants.PRODUCT_VARIANTS)
                .on(OrderItems.ORDER_ITEMS.PRODUCT_VARIANT_ID.eq(ProductVariants.PRODUCT_VARIANTS.ID))
                .join(Products.PRODUCTS)
                .on(ProductVariants.PRODUCT_VARIANTS.PRODUCT_ID.eq(Products.PRODUCTS.ID))
                .where(OrderItems.ORDER_ITEMS.ORDER_ID.`in`(orderIds))
                .fetch()

            val itemsMap = allItems.groupBy { it[OrderItems.ORDER_ITEMS.ORDER_ID]!! }

            val payments = dsl.select(Payments.PAYMENTS.ORDER_ID, Payments.PAYMENTS.STATUS)
                .from(Payments.PAYMENTS)
                .where(Payments.PAYMENTS.ORDER_ID.`in`(orderIds))
                .fetch()
                .associate { row ->
                    val orderId = row[Payments.PAYMENTS.ORDER_ID]
                    val status = row[Payments.PAYMENTS.STATUS]
                    (if (orderId != null && status != null) orderId to status else null)!!
                }


            if (payments.isEmpty()) {
                call.application.environment.log.warn("No payment data found for orders: $orderIds")
            }

            val result = orders.map { order ->
                val items = itemsMap[order.id]?.map { record ->
                    OrderItemResponse(
                        variantId = record[OrderItems.ORDER_ITEMS.PRODUCT_VARIANT_ID] ?: 0L,
                        quantity = record[OrderItems.ORDER_ITEMS.QUANTITY] ?: 0L,
                        price = record[OrderItems.ORDER_ITEMS.PRICE] ?: 0.0,
                        size = record[ProductVariants.PRODUCT_VARIANTS.SIZE]?.toString() ?: "N/A",
                        color = record[ProductVariants.PRODUCT_VARIANTS.COLOR] ?: "N/A",
                        imageUrl = record[ProductVariants.PRODUCT_VARIANTS.IMAGE_URL] ?: "",
                        productName = record[Products.PRODUCTS.NAME] ?: "N/A"
                    )
                } ?: emptyList()

                OrderResponse(
                    id = order.id!!,
                    total = order.total ?: 0.0,
                    status = order.status ?: 0,
                    createdAt = order.createdAt?.toString() ?: "unknown",
                    paymentStatus = payments[order.id]?.name ?: "pending",
                    items = items
                )
            }

            call.respond(result)
        } catch (e: Exception) {
            call.application.environment.log.error("Error fetching user orders", e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }
}
