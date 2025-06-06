/*
 * This file is generated by jOOQ.
 */
package ru.yarsu.db.generated.tables


import javax.annotation.processing.Generated

import kotlin.collections.Collection
import kotlin.collections.List

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Identity
import org.jooq.InverseForeignKey
import org.jooq.Name
import org.jooq.Path
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl

import ru.yarsu.db.generated.Public
import ru.yarsu.db.generated.enums.UserRole
import ru.yarsu.db.generated.keys.DIRECTIONS__DIRECTIONS_CHAIRMAN_ID_FKEY
import ru.yarsu.db.generated.keys.DIRECTIONS__DIRECTIONS_DEPUTY_СHAIRMAN_ID_FKEY
import ru.yarsu.db.generated.keys.COMMENTS__COMMENTS_AUTHORID_FKEY
import ru.yarsu.db.generated.keys.MEDIA__MEDIA_AUTHORID_FKEY
import ru.yarsu.db.generated.keys.POSTS__POSTS_AUTHORID_FKEY
import ru.yarsu.db.generated.keys.POSTS__POSTS_MODERATORID_FKEY
import ru.yarsu.db.generated.keys.USERS_EMAIL_KEY
import ru.yarsu.db.generated.keys.USERS_LOGIN_KEY
import ru.yarsu.db.generated.keys.USERS_PHONENUMBER_KEY
import ru.yarsu.db.generated.keys.USERS_PKEY
import ru.yarsu.db.generated.tables.Directions.DirectionsPath
import ru.yarsu.db.generated.tables.Comments.CommentsPath
import ru.yarsu.db.generated.tables.Media.MediaPath
import ru.yarsu.db.generated.tables.Posts.PostsPath
import ru.yarsu.db.generated.tables.records.UsersRecord


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.10",
        "catalog version:04",
        "schema version:04"
    ],
    comments = "This class is generated by jOOQ"
)
@Suppress("UNCHECKED_CAST")
open class Users(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, UsersRecord>?,
    parentPath: InverseForeignKey<out Record, UsersRecord>?,
    aliased: Table<UsersRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<UsersRecord>(
    alias,
    Public.PUBLIC,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>public.users</code>
         */
        val USERS: Users = Users()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<UsersRecord> = UsersRecord::class.java

    /**
     * The column <code>public.users.id</code>.
     */
    val ID: TableField<UsersRecord, Int?> = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "")

    /**
     * The column <code>public.users.name</code>.
     */
    val NAME: TableField<UsersRecord, String?> = createField(DSL.name("name"), SQLDataType.CHAR(64).nullable(false), this, "")

    /**
     * The column <code>public.users.surname</code>.
     */
    val SURNAME: TableField<UsersRecord, String?> = createField(DSL.name("surname"), SQLDataType.CHAR(64).nullable(false), this, "")

    /**
     * The column <code>public.users.login</code>.
     */
    val LOGIN: TableField<UsersRecord, String?> = createField(DSL.name("login"), SQLDataType.VARCHAR(30).nullable(false), this, "")

    /**
     * The column <code>public.users.email</code>.
     */
    val EMAIL: TableField<UsersRecord, String?> = createField(DSL.name("email"), SQLDataType.VARCHAR(255).nullable(false), this, "")

    /**
     * The column <code>public.users.phonenumber</code>.
     */
    val PHONENUMBER: TableField<UsersRecord, String?> = createField(DSL.name("phonenumber"), SQLDataType.VARCHAR(11).nullable(false), this, "")

    /**
     * The column <code>public.users.password</code>.
     */
    val PASSWORD: TableField<UsersRecord, String?> = createField(DSL.name("password"), SQLDataType.CHAR(64).nullable(false), this, "")

    /**
     * The column <code>public.users.vklink</code>.
     */
    val VKLINK: TableField<UsersRecord, String?> = createField(DSL.name("vklink"), SQLDataType.VARCHAR(255), this, "")

    /**
     * The column <code>public.users.role</code>.
     */
    val ROLE: TableField<UsersRecord, UserRole?> = createField(DSL.name("role"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(UserRole::class.java), this, "")

    private constructor(alias: Name, aliased: Table<UsersRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<UsersRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<UsersRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>public.users</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>public.users</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>public.users</code> table reference
     */
    constructor(): this(DSL.name("users"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, UsersRecord>?, parentPath: InverseForeignKey<out Record, UsersRecord>?): this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, USERS, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class UsersPath : Users, Path<UsersRecord> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, UsersRecord>?, parentPath: InverseForeignKey<out Record, UsersRecord>?): super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<UsersRecord>): super(alias, aliased)
        override fun `as`(alias: String): UsersPath = UsersPath(DSL.name(alias), this)
        override fun `as`(alias: Name): UsersPath = UsersPath(alias, this)
        override fun `as`(alias: Table<*>): UsersPath = UsersPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getIdentity(): Identity<UsersRecord, Int?> = super.getIdentity() as Identity<UsersRecord, Int?>
    override fun getPrimaryKey(): UniqueKey<UsersRecord> = USERS_PKEY
    override fun getUniqueKeys(): List<UniqueKey<UsersRecord>> = listOf(USERS_LOGIN_KEY, USERS_EMAIL_KEY, USERS_PHONENUMBER_KEY)

    private lateinit var _directionsChairmanIdFkey: DirectionsPath

    /**
     * Get the implicit to-many join path to the <code>public.directions</code>
     * table, via the <code>directions_chairman_id_fkey</code> key
     */
    fun directionsChairmanIdFkey(): DirectionsPath {
        if (!this::_directionsChairmanIdFkey.isInitialized)
            _directionsChairmanIdFkey = DirectionsPath(this, null, DIRECTIONS__DIRECTIONS_CHAIRMAN_ID_FKEY.inverseKey)

        return _directionsChairmanIdFkey;
    }

    val directionsChairmanIdFkey: DirectionsPath
        get(): DirectionsPath = directionsChairmanIdFkey()

    private lateinit var _directionsDeputyСhairmanIdFkey: DirectionsPath

    /**
     * Get the implicit to-many join path to the <code>public.directions</code>
     * table, via the <code>directions_deputy_сhairman_id_fkey</code> key
     */
    fun directionsDeputyСhairmanIdFkey(): DirectionsPath {
        if (!this::_directionsDeputyСhairmanIdFkey.isInitialized)
            _directionsDeputyСhairmanIdFkey = DirectionsPath(this, null, DIRECTIONS__DIRECTIONS_DEPUTY_СHAIRMAN_ID_FKEY.inverseKey)

        return _directionsDeputyСhairmanIdFkey;
    }

    val directionsDeputyСhairmanIdFkey: DirectionsPath
        get(): DirectionsPath = directionsDeputyСhairmanIdFkey()

    private lateinit var _comments: CommentsPath

    /**
     * Get the implicit to-many join path to the <code>public.comments</code>
     * table
     */
    fun comments(): CommentsPath {
        if (!this::_comments.isInitialized)
            _comments = CommentsPath(this, null, COMMENTS__COMMENTS_AUTHORID_FKEY.inverseKey)

        return _comments;
    }

    val comments: CommentsPath
        get(): CommentsPath = comments()

    private lateinit var _media: MediaPath

    /**
     * Get the implicit to-many join path to the <code>public.media</code> table
     */
    fun media(): MediaPath {
        if (!this::_media.isInitialized)
            _media = MediaPath(this, null, MEDIA__MEDIA_AUTHORID_FKEY.inverseKey)

        return _media;
    }

    val media: MediaPath
        get(): MediaPath = media()

    private lateinit var _postsAuthoridFkey: PostsPath

    /**
     * Get the implicit to-many join path to the <code>public.posts</code>
     * table, via the <code>posts_authorid_fkey</code> key
     */
    fun postsAuthoridFkey(): PostsPath {
        if (!this::_postsAuthoridFkey.isInitialized)
            _postsAuthoridFkey = PostsPath(this, null, POSTS__POSTS_AUTHORID_FKEY.inverseKey)

        return _postsAuthoridFkey;
    }

    val postsAuthoridFkey: PostsPath
        get(): PostsPath = postsAuthoridFkey()

    private lateinit var _postsModeratoridFkey: PostsPath

    /**
     * Get the implicit to-many join path to the <code>public.posts</code>
     * table, via the <code>posts_moderatorid_fkey</code> key
     */
    fun postsModeratoridFkey(): PostsPath {
        if (!this::_postsModeratoridFkey.isInitialized)
            _postsModeratoridFkey = PostsPath(this, null, POSTS__POSTS_MODERATORID_FKEY.inverseKey)

        return _postsModeratoridFkey;
    }

    val postsModeratoridFkey: PostsPath
        get(): PostsPath = postsModeratoridFkey()
    override fun `as`(alias: String): Users = Users(DSL.name(alias), this)
    override fun `as`(alias: Name): Users = Users(alias, this)
    override fun `as`(alias: Table<*>): Users = Users(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Users = Users(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Users = Users(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Users = Users(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Users = Users(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Users = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Users = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Users = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Users = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Users = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Users = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Users = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Users = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Users = where(DSL.notExists(select))
}
