/*
 * This file is generated by jOOQ.
 */
package ru.yarsu.db.generated.keys


import org.jooq.ForeignKey
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal

import ru.yarsu.db.generated.tables.Media
import ru.yarsu.db.generated.tables.Users
import ru.yarsu.db.generated.tables.records.MediaRecord
import ru.yarsu.db.generated.tables.records.UsersRecord



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val MEDIA_PKEY: UniqueKey<MediaRecord> = Internal.createUniqueKey(Media.MEDIA, DSL.name("media_pkey"), arrayOf(Media.MEDIA.FILENAME), true)
val USERS_EMAIL_KEY: UniqueKey<UsersRecord> = Internal.createUniqueKey(Users.USERS, DSL.name("users_email_key"), arrayOf(Users.USERS.EMAIL), true)
val USERS_LOGIN_KEY: UniqueKey<UsersRecord> = Internal.createUniqueKey(Users.USERS, DSL.name("users_login_key"), arrayOf(Users.USERS.LOGIN), true)
val USERS_PKEY: UniqueKey<UsersRecord> = Internal.createUniqueKey(Users.USERS, DSL.name("users_pkey"), arrayOf(Users.USERS.ID), true)

// -------------------------------------------------------------------------
// FOREIGN KEY definitions
// -------------------------------------------------------------------------

val MEDIA__MEDIA_AUTHORID_FKEY: ForeignKey<MediaRecord, UsersRecord> = Internal.createForeignKey(Media.MEDIA, DSL.name("media_authorid_fkey"), arrayOf(Media.MEDIA.AUTHORID), ru.yarsu.db.generated.keys.USERS_PKEY, arrayOf(Users.USERS.ID), true)
