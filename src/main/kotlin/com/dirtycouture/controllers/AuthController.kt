package com.dirtycouture.controllers

import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.enums.UserRole
import com.dirtycouture.db.generated.tables.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import com.dirtycouture.db.generated.tables.pojos.Users as UserPojo

object AuthController {
    suspend fun register(call: ApplicationCall) {
        val email= call.parameters["email"]
        val password=call.parameters["password"]
        val role=call.parameters["role"]
        if(email==null||password==null||role==null){
            call.respond(HttpStatusCode.BadRequest, "Fields can't be empty!")
            return
        }
        val emailFind= DBFactory.dslContext.selectFrom(Users.USERS).where(Users.USERS.EMAIL.eq(email)).fetchInto(Users::class.java)
        if(emailFind.isEmpty()){
            //Check if userrole is correct
            val userRole = try {
                UserRole.valueOf(role.uppercase())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid role value: $role")
                return
            }
            createUser(email,password,userRole);
            call.respond(HttpStatusCode.Created, "User created successfully")

        }else{
            call.respond(HttpStatusCode.BadRequest, "Email already exists!")
            return
        }
    }

    private fun createUser(email: String, password: String, role: UserRole) {
        val newUser = UserPojo().apply {
            this.email = email
            this.passwordHash = password
            this.role = role
        }

        DBFactory.dslContext
            .insertInto(Users.USERS)
            .set(Users.USERS.EMAIL, newUser.email)
            .set(Users.USERS.PASSWORD_HASH, newUser.passwordHash)
            .set(Users.USERS.ROLE, newUser.role)
            .execute()

    }

    fun login(call: ApplicationCall) {

    }
}