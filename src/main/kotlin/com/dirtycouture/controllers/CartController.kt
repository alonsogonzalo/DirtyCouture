package com.dirtycouture.controllers

import com.dirtycouture.DBFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import com.dirtycouture.db.generated.tables.pojos.CartItems as cartPojos
import com.dirtycouture.db.generated.tables.CartItems
import com.dirtycouture.db.generated.tables.ProductVariants
import com.dirtycouture.db.generated.tables.Products

import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    val variantId: Long,
    val quantity: Double,
    val size: String,
    val color: String,
    val price: Double,
    val imageUrl: String?,
    val productName: String
)

object CartController {

    /**
     * Añade un variant al carrito del usuario.
     * Si ya existe, incrementa la cantidad en 1. Si no, lo inserta con cantidad = 1.
     * Ruta: POST /api/cart/add/{userId}/{variantId}
     */
    suspend fun addVariantToCard(call: ApplicationCall) {
        val idUser = call.parameters["userId"]?.toLongOrNull()
        val productVariantId = call.parameters["variantId"]?.toLongOrNull()

        if (idUser == null || productVariantId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid userId or variantId"))
            return
        }

        val dsl = DBFactory.dslContext

        // Busca si ya existe esa variante en el carrito
        val existing = dsl
            .selectFrom(CartItems.CART_ITEMS)
            .where(
                CartItems.CART_ITEMS.USER_ID.eq(idUser)
                    .and(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(productVariantId))
            )
            .fetchInto(com.dirtycouture.db.generated.tables.pojos.CartItems::class.java)

        if (existing.isEmpty()) {
            // No existía: insertar nuevo
            dsl.insertInto(CartItems.CART_ITEMS)
                .set(CartItems.CART_ITEMS.USER_ID, idUser)
                .set(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID, productVariantId)
                .set(CartItems.CART_ITEMS.QUANTITY, 1.toBigDecimal())
                .execute()

            call.respond(HttpStatusCode.Created, mapOf("message" to "Product added to cart"))
        } else {
            // Ya existía: incrementa cantidad
            val found = existing.first()
            val newQuantity = found.quantity?.plus(1.toBigDecimal()) ?: 1.toBigDecimal()

            dsl.update(CartItems.CART_ITEMS)
                .set(CartItems.CART_ITEMS.QUANTITY, newQuantity)
                .where(
                    CartItems.CART_ITEMS.USER_ID.eq(idUser)
                        .and(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(productVariantId))
                )
                .execute()

            call.respond(HttpStatusCode.OK, mapOf("message" to "Cart updated with new quantity"))
        }
    }

    /**
     * Obtiene todos los items del carrito de un usuario, incluyendo datos de la variante
     * y del producto padre. Retorna List<CartItemResponse>.
     * Ruta: GET /api/cart/{userId}
     */
    suspend fun getCartByIdUser(call: ApplicationCall) {
        val idUser = call.parameters["userId"]?.toLongOrNull()
        if (idUser == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid userId"))
            return
        }

        try {
            val dsl = DBFactory.dslContext
            val result = dsl.select(
                CartItems.CART_ITEMS.PRODUCT_VARIANT_ID,
                CartItems.CART_ITEMS.QUANTITY,
                ProductVariants.PRODUCT_VARIANTS.SIZE,
                ProductVariants.PRODUCT_VARIANTS.COLOR,
                ProductVariants.PRODUCT_VARIANTS.PRICE,
                ProductVariants.PRODUCT_VARIANTS.IMAGE_URL,
                Products.PRODUCTS.NAME
            )
                .from(CartItems.CART_ITEMS)
                .join(ProductVariants.PRODUCT_VARIANTS)
                .on(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(ProductVariants.PRODUCT_VARIANTS.ID))
                .join(Products.PRODUCTS)
                .on(ProductVariants.PRODUCT_VARIANTS.PRODUCT_ID.eq(Products.PRODUCTS.ID))
                .where(CartItems.CART_ITEMS.USER_ID.eq(idUser))
                .fetch()

            val cartItems = result.map { record ->
                CartItemResponse(
                    variantId = record[CartItems.CART_ITEMS.PRODUCT_VARIANT_ID]
                        ?: 0L,
                    quantity = record[CartItems.CART_ITEMS.QUANTITY]
                        ?.toDouble() ?: 0.0,
                    size = record[ProductVariants.PRODUCT_VARIANTS.SIZE]
                        ?.name ?: "",
                    color = record[ProductVariants.PRODUCT_VARIANTS.COLOR]
                        ?: "",
                    price = record[ProductVariants.PRODUCT_VARIANTS.PRICE]
                        ?: 0.0,
                    imageUrl = record[ProductVariants.PRODUCT_VARIANTS.IMAGE_URL],
                    productName = record[Products.PRODUCTS.NAME]
                        ?: ""
                )
            }

            call.respond(HttpStatusCode.OK, cartItems)
        } catch (ex: Exception) {
            call.application.log.error("Error en getCartByIdUser para userId=$idUser", ex)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Failed to fetch cart: ${ex.message}")
            )
        }
    }

    /**
     * Elimina un variant específico del carrito de un usuario.
     * Ruta: DELETE /api/cart/delete/{userId}/{variantId}
     */
    suspend fun deleteVariantOfCard(call: ApplicationCall) {
        val idUser = call.parameters["userId"]?.toLongOrNull()
        val variantId = call.parameters["variantId"]?.toLongOrNull()
        if (idUser == null || variantId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid userId or variantId"))
            return
        }

        val dsl = DBFactory.dslContext
        val deletedCount = dsl.deleteFrom(CartItems.CART_ITEMS)
            .where(
                CartItems.CART_ITEMS.USER_ID.eq(idUser)
                    .and(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(variantId))
            )
            .execute()

        if (deletedCount > 0) {
            call.respond(HttpStatusCode.OK, mapOf("success" to true, "message" to "Item removed from cart"))
        } else {
            call.respond(HttpStatusCode.NotFound, mapOf("success" to false, "message" to "Item not found in cart"))
        }
    }

    /**
     * Elimina todos los items del carrito de un usuario.
     * Ruta: DELETE /api/cart/clear/{userId}
     */
    suspend fun clearCart(call: ApplicationCall) {
        val idUser = call.parameters["userId"]?.toLongOrNull()
        if (idUser == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid userId"))
            return
        }

        val dsl = DBFactory.dslContext
        val deletedCount = dsl.deleteFrom(CartItems.CART_ITEMS)
            .where(CartItems.CART_ITEMS.USER_ID.eq(idUser))
            .execute()

        call.respond(HttpStatusCode.OK, mapOf("success" to true, "deletedItems" to deletedCount))
    }
}