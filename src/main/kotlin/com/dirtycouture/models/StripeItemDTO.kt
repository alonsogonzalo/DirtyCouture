package com.dirtycouture.models

data class StripeItemDTO(
    val name: String,
    val price: Long, //en céntimos
    val quantity: Long
)

/**
 * Payload que el frontend debe enviar para que se cojan bien los datos
 * ---> Para 2 productos en la cesta
 * [
 *   {
 *     "name": "Camiseta",
 *     "price": 20.99,
 *     "quantity": 2
 *   },
 *   {
 *     "name": "Llavero",
 *     "price": 9.99,
 *     "quantity": 1
 *   }
 * ]
 */
