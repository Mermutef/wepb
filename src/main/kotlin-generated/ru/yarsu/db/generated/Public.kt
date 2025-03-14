/*
 * This file is generated by jOOQ.
 */
package ru.yarsu.db.generated


import javax.annotation.processing.Generated

import kotlin.collections.List

import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.SchemaImpl

import ru.yarsu.db.generated.tables.Media
import ru.yarsu.db.generated.tables.Users


/**
 * standard public schema
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.20.2",
        "catalog version:02",
        "schema version:02"
    ],
    comments = "This class is generated by jOOQ"
)
@Suppress("warnings")
open class Public : SchemaImpl(DSL.name("public"), DefaultCatalog.DEFAULT_CATALOG, DSL.comment("standard public schema")) {
    companion object {

        /**
         * The reference instance of <code>public</code>
         */
        val PUBLIC: Public = Public()
    }

    /**
     * The table <code>public.media</code>.
     */
    val MEDIA: Media get() = Media.MEDIA

    /**
     * The table <code>public.users</code>.
     */
    val USERS: Users get() = Users.USERS

    override fun getCatalog(): Catalog = DefaultCatalog.DEFAULT_CATALOG

    override fun getTables(): List<Table<*>> = listOf(
        Media.MEDIA,
        Users.USERS
    )
}
