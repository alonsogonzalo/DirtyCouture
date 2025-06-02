package com.dirtycouture.routes

import io.ktor.server.routing.*
import controllers.NotificationController

fun Route.notificationRoutes(){
    route("/api/notifications"){
        post("/subscribe", NotificationController::subcriptionToAlertStock)
        post("/trigger",  NotificationController::sendEmailToWarmUser)
    }
}
