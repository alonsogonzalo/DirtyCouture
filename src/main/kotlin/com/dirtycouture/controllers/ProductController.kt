package com.dirtycouture.controllers
import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.tables.ProductVariants
import io.ktor.server.application.*
import io.ktor.server.response.*
import com.dirtycouture.db.generated.tables.Products
import io.ktor.http.*


object ProductController {
    suspend fun getAll(call: ApplicationCall) {
        //return list of allproducts
        val products=DBFactory.dslContext.selectFrom(Products.PRODUCTS).fetchInto(Products::class.java)
        call.respond(products)
    }

    suspend fun getById(call: ApplicationCall) {
        val id= call.parameters["id"]?.toLongOrNull()
        if(id==null){
            call.respond(HttpStatusCode.BadRequest, "ID value  null")
            return
        }
        val productFind= DBFactory.dslContext.selectFrom(Products.PRODUCTS).where(Products.PRODUCTS.ID.eq(id)).fetchOneInto(Products::class.java)
        if(productFind==null){
            call.respond(HttpStatusCode.NotFound, "Product not found")
            return
        }else{
            call.respond(productFind)
        }
    }

    suspend fun getVariants(call: ApplicationCall) {
        val id= call.parameters["id"]?.toLongOrNull()
        if(id==null){
            call.respond(HttpStatusCode.BadRequest, "ID value  null")
            return
        }
        val productVariantsFind=  DBFactory.dslContext.selectFrom(ProductVariants.PRODUCT_VARIANTS).where(ProductVariants.PRODUCT_VARIANTS.PRODUCT_ID.eq(id)).fetchInto(ProductVariants::class.java)
        if(productVariantsFind==null){
            call.respond(HttpStatusCode.NotFound, "Product not found")
            return
        }else{
            call.respond(productVariantsFind)
        }
    }

}