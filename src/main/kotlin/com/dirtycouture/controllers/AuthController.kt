package com.dirtycouture.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.github.cdimascio.dotenv.dotenv
import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.tables.Users
import com.dirtycouture.db.generated.tables.records.UsersRecord
import com.dirtycouture.db.generated.enums.UserRole
import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.*

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val id: Long,
    val email: String,
    val role: String,
    val created_at: String
)

@Serializable
data class UserInfo(
    val id: Long,
    val email: String,
    val role: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserInfo
)

object AuthController {
    private val env = dotenv { ignoreIfMissing = true }
    private val jwtSecret = env["JWT_SECRET"]
        ?: throw IllegalStateException("JWT_SECRET no está definido en .env")
    private val jwtIssuer = env["JWT_ISSUER"] ?: "dirtycouture.io"
    private val jwtExpirationMs = (env["JWT_EXPIRATION_MS"] ?: "86400000").toLong()

    /**
     * POST /auth/register
     */
    suspend fun register(call: ApplicationCall) {
        // 1. Parsear JSON…
        val request = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato JSON inválido"))
            return
        }

        // 2. Validar campos…
        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email y contraseña son obligatorios"))
            return
        }

        // 3. Mirar si existe el email (este SELECT, en IO)
        val existingUser: UsersRecord? = withContext(Dispatchers.IO) {
            DBFactory.dslContext
                .selectFrom(Users.USERS)
                .where(Users.USERS.EMAIL.eq(request.email))
                .fetchOneInto(UsersRecord::class.java)
        }
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "Este email ya está registrado"))
            return
        }

        // 4. Hashear…
        val bcryptHash = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

        // 5. Hacer el INSERT (también en IO)
        val createdUser: UsersRecord = try {
            withContext(Dispatchers.IO) {
                DBFactory.dslContext.insertInto(Users.USERS)
                    .set(Users.USERS.EMAIL, request.email)
                    .set(Users.USERS.PASSWORD_HASH, bcryptHash)
                    .set(Users.USERS.CREATED_AT, OffsetDateTime.now())
                    .set(Users.USERS.ROLE, UserRole.User)
                    .returning()
                    .fetchOneInto(UsersRecord::class.java)
                    ?: throw IllegalStateException("Insert succeeded but returned null")
            }
        } catch (e: Exception) {
            val causeMsg = e.cause?.message ?: e.message
            call.respond(HttpStatusCode.InternalServerError,
                mapOf("error" to "No se pudo crear el usuario: $causeMsg"))
            return
        }

        // 6. “Desempaquetar” los nullable y responder
        val userId = createdUser.id ?: run {
            call.respond(HttpStatusCode.InternalServerError,
                mapOf("error" to "ID de usuario nulo"))
            return
        }
        val userEmail = createdUser.email ?: run {
            call.respond(HttpStatusCode.InternalServerError,
                mapOf("error" to "Email de usuario nulo"))
            return
        }
        val userRoleLiteral = createdUser.role?.literal ?: run {
            call.respond(HttpStatusCode.InternalServerError,
                mapOf("error" to "Rol de usuario nulo"))
            return
        }

        val response = RegisterResponse(
            id = userId,
            email = userEmail,
            role = userRoleLiteral,
            created_at = createdUser.createdAt.toString()
        )
        call.respond(HttpStatusCode.Created, response)
    }

    suspend fun login(call: ApplicationCall) {
        val request = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato JSON inválido"))
            return
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email y contraseña son obligatorios"))
            return
        }

        // Buscar usuario (en IO)
        val user: UsersRecord? = withContext(Dispatchers.IO) {
            DBFactory.dslContext
                .selectFrom(Users.USERS)
                .where(Users.USERS.EMAIL.eq(request.email))
                .fetchOneInto(UsersRecord::class.java)
        }
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciales inválidas"))
            return
        }

        // Verificar bcrypt
        val result = BCrypt.verifyer().verify(request.password.toCharArray(), user.passwordHash)
        if (!result.verified) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciales inválidas"))
            return
        }

        val userId: Long = user.id ?: run {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "ID de usuario nulo"))
            return
        }
        val email: String = user.email ?: run {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Email de usuario nulo"))
            return
        }
        val role: String = user.role?.literal ?: run {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Rol de usuario nulo"))
            return
        }

        // Generar JWT (no necesita IO)
        val token = generateJwtToken(userId, email, role)

        val userInfo = UserInfo(id = userId, email = email, role = role)
        val loginResponse = LoginResponse(token = token, user = userInfo)
        call.respond(HttpStatusCode.OK, loginResponse)
    }

    private fun generateJwtToken(userId: Long, email: String, role: String): String {
        val algorithm = Algorithm.HMAC256(jwtSecret)
        val nowMillis = System.currentTimeMillis()
        val expMillis = nowMillis + jwtExpirationMs
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("role", role)
            .withIssuedAt(Date(nowMillis))
            .withExpiresAt(Date(expMillis))
            .sign(algorithm)
    }
}
