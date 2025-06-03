package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.AuthController
import io.ktor.server.application.*

fun Route.authRoutes(){
    route("/auth"){
        post("/register"){AuthController.register(call)}
        post("/login"){AuthController.login(call)}
    }
}