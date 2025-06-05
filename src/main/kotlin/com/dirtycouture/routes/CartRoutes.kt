package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.CartController
import io.ktor.server.application.*

fun Route.cartRoutes() {
    route("/cart") {
        post("/add/{userId}/{variantId}") {
            CartController.addVariantToCard(call)
        }
        get("/{userId}") {
            CartController.getCartByIdUser(call)
        }
        delete("/delete/{userId}/{variantId}") {
            CartController.deleteVariantOfCard(call)
        }
        delete("/clear/{userId}") {
            CartController.clearCart(call)
        }
    }
}
