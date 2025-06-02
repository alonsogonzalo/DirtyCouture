package com.dirtycouture.routes

import io.ktor.server.routing.*
import controllers.AuthController

fun Route.authRoutes(){
    route("/auth"){
        post("/register", AuthController::register)
        post("/login", AuthController::login)
    }
}