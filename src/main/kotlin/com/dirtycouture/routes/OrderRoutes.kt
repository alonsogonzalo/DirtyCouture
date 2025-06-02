package com.dirtycouture.routes

import io.ktor.server.routing.*
import controllers.OrderController

fun Route.orderRoutes(){
    route("/api/orders"){
        post("{cardId}", OrderController::addOrderHome)
        get("{userId}", OrderController:: getAllOrderByIdUser)
    }
}