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
        val idUser= call.parameters["userId"]?.toLong()
        if(idUser==null){
            call.respond(HttpStatusCode.BadRequest, "Empty fields")
            return
        }
        val cardFindByIdUser= DBFactory.dslContext.selectFrom(CartItems.CART_ITEMS)
            .where(CartItems.CART_ITEMS.USER_ID.eq(idUser))
            .fetchInto(CartItems::class.java)

        if(cardFindByIdUser.isEmpty()){
            call.respond(HttpStatusCode.BadRequest, "User don't have cart")
            return
        }
        call.respond(cardFindByIdUser);
    }

    suspend fun deleteVariantOfCard(call: ApplicationCall) {
        val idUser = call.parameters["userId"]?.toLong()
        val idCart= call.parameters["cartId"]?.toLong()
        if(idUser==null||idCart==null){
            call.respond(HttpStatusCode.BadRequest, "Empty fields")
            return
        }
        val cardFind= DBFactory.dslContext.selectFrom(CartItems.CART_ITEMS)
            .where(CartItems.CART_ITEMS.USER_ID.eq(idUser)
                .and(CartItems.CART_ITEMS.ID.eq(idCart)))
            .fetchInto(CartItems::class.java)
        if(cardFind.isEmpty()){
            call.respond(HttpStatusCode.BadRequest, "User don't have cart")
            return
        }else{
            DBFactory.dslContext.deleteFrom(CartItems.CART_ITEMS)
                .where(CartItems.CART_ITEMS.USER_ID.eq(idUser)
                .and(CartItems.CART_ITEMS.ID.eq(idCart))).execute()

            call.respond(HttpStatusCode.OK, "Cart item deleted successfully")
        }
    }

}