package com.dirtycouture

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.sql.Connection
import io.github.cdimascio.dotenv.dotenv

object DBFactory {
    private lateinit var hikariDataSource: HikariDataSource
    lateinit var dslContext: DSLContext

    fun init() {
        val dotenv = dotenv {
            ignoreIfMissing = true
        }

        val jdbcUrl = dotenv["DB_URL"] ?: System.getenv("DB_URL")
        val user = dotenv["DB_USER"] ?: System.getenv("DB_USER")
        val password = dotenv["DB_PASS"] ?: System.getenv("DB_PASS")

        require(!jdbcUrl.isNullOrBlank()) { "DB_URL no está definido" }
        require(!user.isNullOrBlank()) { "DB_USER no está definido" }
        require(!password.isNullOrBlank()) { "DB_PASS no está definido" }

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = user
            this.password = password
            maximumPoolSize = 10
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        hikariDataSource = HikariDataSource(config)
        dslContext = DSL.using(hikariDataSource, org.jooq.SQLDialect.POSTGRES)
    }

    fun getConnection(): Connection = hikariDataSource.connection
}