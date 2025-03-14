/*
 * This file is generated by jOOQ.
 */
package ru.yarsu.db.generated.tables.records


import javax.annotation.processing.Generated

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl

import ru.yarsu.db.generated.enums.UserRole
import ru.yarsu.db.generated.tables.Users


/**
 * This class is generated by jOOQ.
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
open class UsersRecord() : UpdatableRecordImpl<UsersRecord>(Users.USERS) {

    open var id: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open var name: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var email: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var password: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    open var role: UserRole?
        set(value): Unit = set(4, value)
        get(): UserRole? = get(4) as UserRole?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    /**
     * Create a detached, initialised UsersRecord
     */
    constructor(id: Int? = null, name: String? = null, email: String? = null, password: String? = null, role: UserRole? = null): this() {
        this.id = id
        this.name = name
        this.email = email
        this.password = password
        this.role = role
        resetTouchedOnNotNull()
    }
}
