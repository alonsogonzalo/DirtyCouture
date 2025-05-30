/*
 * This file is generated by jOOQ.
 */
package com.dirtycouture.db.generated.tables


import com.dirtycouture.db.generated.Public
import com.dirtycouture.db.generated.keys.CART_ITEMS_PKEY
import com.dirtycouture.db.generated.keys.CART_ITEMS__CART_ITEMS_PRODUCT_VARIANT_ID_FKEY
import com.dirtycouture.db.generated.keys.CART_ITEMS__CART_ITEMS_USER_ID_FKEY
import com.dirtycouture.db.generated.tables.records.CartItemsRecord

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.function.Function

import kotlin.collections.List

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Identity
import org.jooq.Name
import org.jooq.Record
import org.jooq.Records
import org.jooq.Row5
import org.jooq.Schema
import org.jooq.SelectField
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class CartItems(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, CartItemsRecord>?,
    aliased: Table<CartItemsRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<CartItemsRecord>(
    alias,
    Public.PUBLIC,
    child,
    path,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table()
) {
    companion object {

        /**
         * The reference instance of <code>public.cart_items</code>
         */
        val CART_ITEMS: CartItems = CartItems()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<CartItemsRecord> = CartItemsRecord::class.java

    /**
     * The column <code>public.cart_items.id</code>.
     */
    val ID: TableField<CartItemsRecord, Long?> = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "")

    /**
     * The column <code>public.cart_items.user_id</code>.
     */
    val USER_ID: TableField<CartItemsRecord, Long?> = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "")

    /**
     * The column <code>public.cart_items.product_variant_id</code>.
     */
    val PRODUCT_VARIANT_ID: TableField<CartItemsRecord, Long?> = createField(DSL.name("product_variant_id"), SQLDataType.BIGINT.nullable(false), this, "")

    /**
     * The column <code>public.cart_items.quantity</code>.
     */
    val QUANTITY: TableField<CartItemsRecord, BigDecimal?> = createField(DSL.name("quantity"), SQLDataType.NUMERIC.nullable(false), this, "")

    /**
     * The column <code>public.cart_items.added_at</code>.
     */
    val ADDED_AT: TableField<CartItemsRecord, OffsetDateTime?> = createField(DSL.name("added_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false).defaultValue(DSL.field("(now() AT TIME ZONE 'utc'::text)", SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "")

    private constructor(alias: Name, aliased: Table<CartItemsRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<CartItemsRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>public.cart_items</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>public.cart_items</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>public.cart_items</code> table reference
     */
    constructor(): this(DSL.name("cart_items"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, CartItemsRecord>): this(Internal.createPathAlias(child, key), child, key, CART_ITEMS, null)
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getIdentity(): Identity<CartItemsRecord, Long?> = super.getIdentity() as Identity<CartItemsRecord, Long?>
    override fun getPrimaryKey(): UniqueKey<CartItemsRecord> = CART_ITEMS_PKEY
    override fun getReferences(): List<ForeignKey<CartItemsRecord, *>> = listOf(CART_ITEMS__CART_ITEMS_USER_ID_FKEY, CART_ITEMS__CART_ITEMS_PRODUCT_VARIANT_ID_FKEY)

    private lateinit var _users: Users
    private lateinit var _productVariants: ProductVariants

    /**
     * Get the implicit join path to the <code>public.users</code> table.
     */
    fun users(): Users {
        if (!this::_users.isInitialized)
            _users = Users(this, CART_ITEMS__CART_ITEMS_USER_ID_FKEY)

        return _users;
    }

    val users: Users
        get(): Users = users()

    /**
     * Get the implicit join path to the <code>public.product_variants</code>
     * table.
     */
    fun productVariants(): ProductVariants {
        if (!this::_productVariants.isInitialized)
            _productVariants = ProductVariants(this, CART_ITEMS__CART_ITEMS_PRODUCT_VARIANT_ID_FKEY)

        return _productVariants;
    }

    val productVariants: ProductVariants
        get(): ProductVariants = productVariants()
    override fun `as`(alias: String): CartItems = CartItems(DSL.name(alias), this)
    override fun `as`(alias: Name): CartItems = CartItems(alias, this)
    override fun `as`(alias: Table<*>): CartItems = CartItems(alias.getQualifiedName(), this)

    /**
     * Rename this table
     */
    override fun rename(name: String): CartItems = CartItems(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): CartItems = CartItems(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): CartItems = CartItems(name.getQualifiedName(), null)

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------
    override fun fieldsRow(): Row5<Long?, Long?, Long?, BigDecimal?, OffsetDateTime?> = super.fieldsRow() as Row5<Long?, Long?, Long?, BigDecimal?, OffsetDateTime?>

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    fun <U> mapping(from: (Long?, Long?, Long?, BigDecimal?, OffsetDateTime?) -> U): SelectField<U> = convertFrom(Records.mapping(from))

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    fun <U> mapping(toType: Class<U>, from: (Long?, Long?, Long?, BigDecimal?, OffsetDateTime?) -> U): SelectField<U> = convertFrom(toType, Records.mapping(from))
}
