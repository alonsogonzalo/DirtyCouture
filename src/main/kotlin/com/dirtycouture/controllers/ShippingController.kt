package com.dirtycouture.controllers

import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.tables.ShippingAddresses
import com.dirtycouture.db.generated.tables.pojos.ShippingAddresses as ShippingPojo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter

@Serializable
data class AddAddressRequest(
    val fullName: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val phoneNumber: String
)

@Serializable
data class ShippingAddressResponse(
    val id: Long,
    val userId: Long,
    val fullName: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val phoneNumber: String,
    val createdAt: String // lo devolvemos como String ISO
)

object ShippingController {

    /**
     * GET /api/shipping/{userId}
     * Devuelve todas las direcciones guardadas para el usuario indicado.
     */
    /** GET /api/shipping/{userId} */
    suspend fun getUserAddresses(call: ApplicationCall) {
        val userId = call.parameters["userId"]?.toLongOrNull()
        if (userId == null) {
            return call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid userId"))
        }

        try {
            val dsl = DBFactory.dslContext
            val records: List<ShippingPojo> = dsl.selectFrom(ShippingAddresses.SHIPPING_ADDRESSES)
                .where(ShippingAddresses.SHIPPING_ADDRESSES.USER_ID.eq(userId))
                .fetchInto(ShippingPojo::class.java)

            // Convertimos cada ShippingPojo a ShippingAddressResponse
            val responseList = records.map { pojo ->
                ShippingAddressResponse(
                    id = pojo.id ?: 0L,
                    userId = pojo.userId ?: 0L,
                    fullName = pojo.fullName ?: "",
                    address = pojo.address ?: "",
                    city = pojo.city ?: "",
                    state = pojo.state ?: "",
                    zipCode = pojo.zipCode ?: "",
                    country = pojo.country ?: "",
                    phoneNumber = pojo.phoneNumber ?: "",
                    // createdAt viene como OffsetDateTime?; lo convertimos a String ISO
                    createdAt = pojo.createdAt
                        ?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        ?: ""
                )
            }

            call.respond(responseList)
        } catch (ex: Exception) {
            call.application.log.error("Error en getUserAddresses para userId=$userId", ex)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to fetch addresses"))
        }
    }

    /** POST /api/shipping */
    suspend fun addAddress(call: ApplicationCall) {
        // 1) Sacar userId del JWT
        val principal = call.principal<JWTPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
        val userId = principal.payload.subject?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid JWT subject"))

        // 2) Deserializar el JSON a AddAddressRequest
        val request: AddAddressRequest = try {
            call.receive()
        } catch (ex: Exception) {
            call.application.log.error("Error parsing AddAddressRequest", ex)
            return call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
        }

        try {
            // 3) Insertar la nueva dirección y retornar el POJO completo
            val dsl = DBFactory.dslContext
            val createdPojo: ShippingPojo? = dsl.insertInto(ShippingAddresses.SHIPPING_ADDRESSES)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.USER_ID, userId)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.FULL_NAME, request.fullName)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.ADDRESS, request.address)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.CITY, request.city)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.STATE, request.state)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.ZIP_CODE, request.zipCode)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.COUNTRY, request.country)
                .set(ShippingAddresses.SHIPPING_ADDRESSES.PHONE_NUMBER, request.phoneNumber)
                .returning()
                .fetchOneInto(ShippingPojo::class.java)

            if (createdPojo == null) {
                return call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Could not create address"))
            }

            // 4) Convertimos el POJO recién creado a nuestro DTO serializable
            val responseDto = ShippingAddressResponse(
                id = createdPojo.id ?: 0L,
                userId = createdPojo.userId ?: 0L,
                fullName = createdPojo.fullName ?: "",
                address = createdPojo.address ?: "",
                city = createdPojo.city ?: "",
                state = createdPojo.state ?: "",
                zipCode = createdPojo.zipCode ?: "",
                country = createdPojo.country ?: "",
                phoneNumber = createdPojo.phoneNumber ?: "",
                createdAt = createdPojo.createdAt
                    ?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    ?: ""
            )

            call.respond(HttpStatusCode.Created, responseDto)
        } catch (ex: Exception) {
            call.application.log.error("Error en addAddress para userId=$userId", ex)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to save address"))
        }
    }
}