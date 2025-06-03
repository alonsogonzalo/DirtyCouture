package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.CartController
import io.ktor.server.application.*

fun Route.cartRoutes(){
    route("/api/cart"){
        post("{variantId}"){CartController.addVariantToCard(call)}
        get("{userId}"){CartController.getCartByIdUser(call)}
        delete("{userId}/{variantId}"){CartController.deleteVariantOfCard(call)}
    }
}