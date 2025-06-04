package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.OrderController
import io.ktor.server.application.*

fun Route.orderRoutes(){
    route("/api/orders"){
        post("{userId}/{orderId}"){ OrderController.getActuallyOrder(call)}
        get("{userId}"){ OrderController.getAllOrderByIdUser(call)}
        get("/user") {
            OrderController.getOrdersWithItems(call)
        }
    }
}