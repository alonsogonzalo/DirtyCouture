package com.dirtycouture.routes

import io.ktor.server.routing.*
import controllers.ProductController

fun Route.productRoutes(){
    route("/api/products"){
        get("", ProductController::getAll)
        get("{id}", ProductController::getById)
        get("{id}/variants", ProductController::getVariants)
    }
}