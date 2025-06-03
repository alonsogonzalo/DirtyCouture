package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.ProductController
import io.ktor.server.application.*

fun Route.productRoutes(){
    route("/api/products"){
        get(""){ProductController.getAll(call)}
        get("{id}"){ ProductController.getById(call)}
        get("{id}/variants"){ProductController.getVariants(call)}
    }
}