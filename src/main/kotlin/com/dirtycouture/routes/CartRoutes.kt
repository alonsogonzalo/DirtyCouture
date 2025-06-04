package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.CartController
import io.ktor.server.application.*

fun Route.cartRoutes(){
    route("/api/cart"){
        post("/add/{userId}/{variantId}"){CartController.addVariantToCard(call)}
        get("{userId}"){CartController.getCartByIdUser(call)}
        delete("/delete/{userId}/{cartId}"){CartController.deleteVariantOfCard(call)}
    }
}