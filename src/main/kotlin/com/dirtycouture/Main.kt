package com.dirtycouture

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
//import com.dirtycouture.routes.registerRoutes <- Puede cambiar de nombre

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DBFactory.init()

    install(ContentNegotiation) {
        json()
    }

    routing {
        //registerRouter()
    }
}