package com.dirtycouture

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.sql.Connection

object DBFactory {
    private lateinit var hikariDataSource: HikariDataSource
    lateinit var dslContext: DSLContext

    fun init() {
        val config = HikariConfig().apply {
            var jdbcUrl = System.getenv("DB_URL")
            username = System.getenv("DB_USER")
            password = System.getenv("DB_PASSWORD")
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            //valida()
        }

        hikariDataSource = HikariDataSource(config)
        dslContext = DSL.using(hikariDataSource, org.jooq.SQLDialect.POSTGRES)
    }

    fun getConnection(): Connection = hikariDataSource.connection
}