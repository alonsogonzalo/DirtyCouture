package com.dirtycouture.routes

import io.ktor.server.routing.*
import controllers.CartController

fun Route.cartRoutes(){
    route("/api/cart"){
        post("{variantId}", CartController::addVariantToCard)
        get("{userId}", CartController::getCartByIdUser)
        delete("{userId}/{variantId}", CartController::deleteVariantOfCard)
    }
}