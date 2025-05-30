/*
 * This file is generated by jOOQ.
 */
package com.dirtycouture.db.generated.tables.daos


import com.dirtycouture.db.generated.tables.Points
import com.dirtycouture.db.generated.tables.records.PointsRecord

import java.time.OffsetDateTime

import kotlin.collections.List

import org.jooq.Configuration
import org.jooq.impl.DAOImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class PointsDao(configuration: Configuration?) : DAOImpl<PointsRecord, com.dirtycouture.db.generated.tables.pojos.Points, Long>(Points.POINTS, com.dirtycouture.db.generated.tables.pojos.Points::class.java, configuration) {

    /**
     * Create a new PointsDao without any configuration
     */
    constructor(): this(null)

    override fun getId(o: com.dirtycouture.db.generated.tables.pojos.Points): Long? = o.id

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfId(lowerInclusive: Long?, upperInclusive: Long?): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetchRange(Points.POINTS.ID, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    fun fetchById(vararg values: Long): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetch(Points.POINTS.ID, *values.toTypedArray())

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    fun fetchOneById(value: Long): com.dirtycouture.db.generated.tables.pojos.Points? = fetchOne(Points.POINTS.ID, value)

    /**
     * Fetch records that have <code>added_at BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfAddedAt(lowerInclusive: OffsetDateTime?, upperInclusive: OffsetDateTime?): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetchRange(Points.POINTS.ADDED_AT, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>added_at IN (values)</code>
     */
    fun fetchByAddedAt(vararg values: OffsetDateTime): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetch(Points.POINTS.ADDED_AT, *values)

    /**
     * Fetch records that have <code>user_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfUserId(lowerInclusive: Long?, upperInclusive: Long?): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetchRange(Points.POINTS.USER_ID, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>user_id IN (values)</code>
     */
    fun fetchByUserId(vararg values: Long): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetch(Points.POINTS.USER_ID, *values.toTypedArray())

    /**
     * Fetch records that have <code>last_added_points BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    fun fetchRangeOfLastAddedPoints(lowerInclusive: Long?, upperInclusive: Long?): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetchRange(Points.POINTS.LAST_ADDED_POINTS, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>last_added_points IN (values)</code>
     */
    fun fetchByLastAddedPoints(vararg values: Long): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetch(Points.POINTS.LAST_ADDED_POINTS, *values.toTypedArray())

    /**
     * Fetch records that have <code>points_balance BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    fun fetchRangeOfPointsBalance(lowerInclusive: Long?, upperInclusive: Long?): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetchRange(Points.POINTS.POINTS_BALANCE, lowerInclusive, upperInclusive)

    /**
     * Fetch records that have <code>points_balance IN (values)</code>
     */
    fun fetchByPointsBalance(vararg values: Long): List<com.dirtycouture.db.generated.tables.pojos.Points> = fetch(Points.POINTS.POINTS_BALANCE, *values.toTypedArray())
}
