package com.dirtycouture.services

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.auth.jwt.*

val USER_ID_KEY = AttributeKey<Long>("userId")

fun ApplicationCall.extractUserIdFromToken() {
    val principal = this.principal<JWTPrincipal>()
    val userId = principal?.payload?.subject?.toLongOrNull()
    if (userId != null) {
        this.attributes.put(USER_ID_KEY, userId)
    }
}
