package com.dirtycouture.routes

import io.ktor.server.routing.*
import com.dirtycouture.controllers.NotificationController
import io.ktor.server.application.*

fun Route.notificationRoutes(){
    route("/api/notifications"){
        post("/subscribe") {NotificationController.subcriptionToAlertStock(call)}
        post("/trigger"){ NotificationController.sendEmailToWarmUser(call)}
    }
}
