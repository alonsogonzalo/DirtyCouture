package com.dirtycouture.controllers

import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.tables.CartItems
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import com.dirtycouture.db.generated.tables.pojos.CartItems as cartPojos
object CartController {

    suspend fun addVariantToCard(call: ApplicationCall) {
        val idUser = call.parameters["userId"]?.toLong()
        val productVariant_id = call.parameters["variantId"]?.toLong()


        if (idUser == null || productVariant_id == null) {
            call.respond(HttpStatusCode.BadRequest, "Empty fields")
            return
        }

        val findProducts = DBFactory.dslContext.selectFrom(CartItems.CART_ITEMS).where(CartItems.CART_ITEMS.USER_ID.eq(idUser).and(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(productVariant_id))).fetchInto(cartPojos::class.java)

        if (findProducts.isEmpty()) {

            val newProduct = cartPojos().apply {
                this.quantity=1?.toBigDecimal()
                this.productVariantId= productVariant_id
                this.userId= idUser
            }
            DBFactory.dslContext
                .insertInto(CartItems.CART_ITEMS)
                .set(CartItems.CART_ITEMS.USER_ID, newProduct.userId)
                .set(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID, newProduct.productVariantId)
                .set(CartItems.CART_ITEMS.QUANTITY, newProduct.quantity)
                .execute()

            call.respond(HttpStatusCode.Created, "Product added to cart")
        } else {
            val firstFindProduct = findProducts.first()
            val newProduct = cartPojos().apply {
                this.quantity= firstFindProduct.quantity?.plus(1.toBigDecimal())
                this.productVariantId= firstFindProduct.productVariantId
                this.userId= firstFindProduct.userId;
            }


            DBFactory.dslContext.update(CartItems.CART_ITEMS)
                .set(CartItems.CART_ITEMS.QUANTITY, newProduct.quantity)
                .where(CartItems.CART_ITEMS.USER_ID.eq(idUser).and(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(productVariant_id)))
                .execute()

            call.respond(HttpStatusCode.OK, "Cart updated with new quantity")
        }
    }


    suspend fun getCartByIdUser(call: ApplicationCall) {
        val userId = call.parameters["userId"]?.toLongOrNull()
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("success" to false, "message" to "Missing userId"))
            return
        }

        val result = DBFactory.dslContext.select(
            CartItems.CART_ITEMS.ID,
            CartItems.CART_ITEMS.PRODUCT_VARIANT_ID,
            CartItems.CART_ITEMS.QUANTITY,
            com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.SIZE,
            com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.COLOR,
            com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.PRICE,
            com.dirtycouture.db.generated.tables.Products.PRODUCTS.NAME
        )
            .from(CartItems.CART_ITEMS)
            .join(com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS)
            .on(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.ID))
            .join(com.dirtycouture.db.generated.tables.Products.PRODUCTS)
            .on(com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.PRODUCT_ID.eq(com.dirtycouture.db.generated.tables.Products.PRODUCTS.ID))
            .where(CartItems.CART_ITEMS.USER_ID.eq(userId))
            .fetch()

        val cartItems = result.map {
            mapOf(
                "cartItemId" to it[CartItems.CART_ITEMS.ID],
                "variantId" to it[CartItems.CART_ITEMS.PRODUCT_VARIANT_ID],
                "quantity" to it[CartItems.CART_ITEMS.QUANTITY],
                "size" to it[com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.SIZE],
                "color" to it[com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.COLOR],
                "price" to it[com.dirtycouture.db.generated.tables.ProductVariants.PRODUCT_VARIANTS.PRICE],
                "productName" to it[com.dirtycouture.db.generated.tables.Products.PRODUCTS.NAME]
            )
        }

        call.respond(HttpStatusCode.OK, cartItems)
    }


    suspend fun deleteVariantOfCard(call: ApplicationCall) {
        val idUser = call.parameters["userId"]?.toLong()
        val variantId = call.parameters["variantId"]?.toLong()
        if (idUser == null || variantId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("success" to false, "message" to "Missing parameters"))
            return
        }

        val deleted = DBFactory.dslContext.deleteFrom(CartItems.CART_ITEMS)
            .where(CartItems.CART_ITEMS.USER_ID.eq(idUser)
                .and(CartItems.CART_ITEMS.PRODUCT_VARIANT_ID.eq(variantId)))
            .execute()

        if (deleted > 0) {
            call.respond(HttpStatusCode.OK, mapOf("success" to true, "message" to "Item removed from cart"))
        } else {
            call.respond(HttpStatusCode.NotFound, mapOf("success" to false, "message" to "Item not found in cart"))
        }
    }


}