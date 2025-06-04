package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.AuthController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import com.dirtycouture.services.extractUserIdFromToken

fun Route.authRoutes() {
    route("/auth") {
        post("/register") { AuthController.register(call) }
        post("/login") { AuthController.login(call) }

        authenticate("auth-jwt") {
            put("/update") {
                call.extractUserIdFromToken()
                AuthController.updateUser(call)
            }
        }
    }
}
