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
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

/**
 * DTO para el JSON de registro:
 * {
 *   "email": "usuario@example.com",
 *   "password": "MiPasswordSegura123"
 * }
 */
data class RegisterRequest(
    val email: String,
    val password: String
)

/**
 * DTO para el JSON de login:
 * {
 *   "email": "usuario@example.com",
 *   "password": "MiPasswordSegura123"
 * }
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * AuthController: maneja registro y login de usuarios,
 * usando la tabla users(id, email, password_hash, created_at, role).
 */
object AuthController {

    // Cargamos variables de entorno (archivo .env)
    private val env = dotenv {
        ignoreIfMissing = true
    }

    // Secret para firmar los tokens JWT
    private val jwtSecret = env["JWT_SECRET"]
        ?: throw IllegalStateException("JWT_SECRET no está definido en .env")
    // Issuer (puede usarse en la validación del token)
    private val jwtIssuer = env["JWT_ISSUER"] ?: "dirtycouture.io"
    // Duración en milisegundos (por defecto 24h = 86_400_000 ms)
    private val jwtExpirationMs = (env["JWT_EXPIRATION_MS"] ?: "86400000").toLong()

    /**
     * POST /auth/register
     * Recibe RegisterRequest { email, password }.
     * Hashea la contraseña y crea un nuevo registro en la tabla users.
     */
    suspend fun register(call: ApplicationCall) {
        // 1. Parseamos JSON a RegisterRequest
        val request = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato JSON inválido"))
            return
        }

        // 2. Validamos campos no vacíos
        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email y contraseña son obligatorios"))
            return
        }

        // 3. Verificamos si ya existe un usuario con ese email
        val existingUser: UsersRecord? = DBFactory.dslContext
            .selectFrom(Users.USERS)
            .where(Users.USERS.EMAIL.eq(request.email))
            .fetchOneInto(UsersRecord::class.java)

        if (existingUser != null) {
            // 4. Si ya existe, devolvemos 409 Conflict
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "Este email ya está registrado"))
            return
        }

        // 5. Hasheamos la contraseña con Bcrypt (cost factor 12)
        val bcryptHash = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

        // 6. Insertamos en la tabla users:
        //    columnas: email, password_hash, created_at, role
        val createdUser: UsersRecord = DBFactory.dslContext
            .insertInto(Users.USERS)
            .set(Users.USERS.EMAIL, request.email)
            .set(Users.USERS.PASSWORD_HASH, bcryptHash)
            .set(Users.USERS.CREATED_AT, OffsetDateTime.now())
            .set(Users.USERS.ROLE, UserRole.User) // Asignamos rol por defecto
            .returning()
            .fetchOneInto(UsersRecord::class.java)!!

        // 7. Devolvemos 201 Created con información mínima (sin password_hash)
        call.respond(HttpStatusCode.Created, mapOf(
            "id"         to createdUser.id,
            "email"      to createdUser.email,
            "role"       to createdUser.role,
            "created_at" to createdUser.createdAt.toString()
        ))
    }

    /**
     * POST /auth/login
     * Recibe LoginRequest { email, password }.
     * Verifica credenciales y, si son correctas, devuelve un JWT.
     */
    suspend fun login(call: ApplicationCall) {
        // 1. Parseamos JSON a LoginRequest
        val request = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato JSON inválido"))
            return
        }

        // 2. Validamos que no estén vacíos
        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email y contraseña son obligatorios"))
            return
        }

        // 3. Buscamos usuario por email
        val user: UsersRecord? = DBFactory.dslContext
            .selectFrom(Users.USERS)
            .where(Users.USERS.EMAIL.eq(request.email))
            .fetchOneInto(UsersRecord::class.java)

        if (user == null) {
            // 4. Si no existe, 401 Unauthorized
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciales inválidas"))
            return
        }

        // 5. Verificamos la contraseña con Bcrypt
        val result = BCrypt.verifyer().verify(request.password.toCharArray(), user.passwordHash)
        if (!result.verified) {
            // 6. Si no coincide, 401 Unauthorized
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciales inválidas"))
            return
        }

        // 7. Comprobamos que id, email y role no sean null antes de generar el token
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

        // 8. Generamos el token JWT
        val token = generateJwtToken(userId, email, role)

        // 9. Devolvemos 200 OK con token y datos del usuario
        call.respond(HttpStatusCode.OK, mapOf(
            "token" to token,
            "user"  to mapOf(
                "id"    to userId,
                "email" to email,
                "role"  to role
            )
        ))
    }

    /**
     * Genera un token JWT firmado con HMAC256.
     * Claims incluidos:
     * - issuer (iss)
     * - subject (sub) = userId como String
     * - email
     * - role
     * - issuedAt (iat)
     * - expiresAt (exp)
     */
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
