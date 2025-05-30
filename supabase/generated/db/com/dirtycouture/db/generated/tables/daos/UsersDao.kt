/*
 * This file is generated by jOOQ.
 */
package com.dirtycouture.db.generated.tables.daos


import com.dirtycouture.db.generated.enums.UserRole
import com.dirtycouture.db.generated.tables.Users
import com.dirtycouture.db.generated.tables.records.UsersRecord

import java.time.OffsetDateTime

import kotlin.collections.List

import org.jooq.Configuration
import org.jooq.impl.DAOImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class UsersDao(configuration: Configuration?) : DAOImpl<UsersRecord, com.dirtycouture.db.generated.tables.pojos.Users, Long>(Users.USERS, com.dirtycouture.db.generated.tables.pojos.Users::class.java, configuration) {

    /**
     * Create a new UsersDao without any configuration
     */
    constructor(): this(null)

    override fun getId(o: com.dirtycouture.db.generated.tables.pojos.Users): Long? = o.id

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfId(lowerInclusive: Long?, upperInclusive: Long?): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetchRange(Users.USERS.ID, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    fun fetchById(vararg values: Long): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetch(Users.USERS.ID, *values.toTypedArray())

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    fun fetchOneById(value: Long): com.dirtycouture.db.generated.tables.pojos.Users? = fetchOne(Users.USERS.ID, value)

    /**
     * Fetch records that have <code>email BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfEmail(lowerInclusive: String?, upperInclusive: String?): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetchRange(Users.USERS.EMAIL, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>email IN (values)</code>
     */
    fun fetchByEmail(vararg values: String): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetch(Users.USERS.EMAIL, *values)

    /**
     * Fetch a unique record that has <code>email = value</code>
     */
    fun fetchOneByEmail(value: String): com.dirtycouture.db.generated.tables.pojos.Users? = fetchOne(Users.USERS.EMAIL, value)

    /**
     * Fetch records that have <code>password_hash BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfPasswordHash(lowerInclusive: String?, upperInclusive: String?): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetchRange(Users.USERS.PASSWORD_HASH, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>password_hash IN (values)</code>
     */
    fun fetchByPasswordHash(vararg values: String): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetch(Users.USERS.PASSWORD_HASH, *values)

    /**
     * Fetch records that have <code>created_at BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfCreatedAt(lowerInclusive: OffsetDateTime?, upperInclusive: OffsetDateTime?): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetchRange(Users.USERS.CREATED_AT, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>created_at IN (values)</code>
     */
    fun fetchByCreatedAt(vararg values: OffsetDateTime): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetch(Users.USERS.CREATED_AT, *values)

    /**
     * Fetch records that have <code>role BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfRole(lowerInclusive: UserRole?, upperInclusive: UserRole?): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetchRange(Users.USERS.ROLE, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>role IN (values)</code>
     */
    fun fetchByRole(vararg values: UserRole): List<com.dirtycouture.db.generated.tables.pojos.Users> = fetch(Users.USERS.ROLE, *values)
}
