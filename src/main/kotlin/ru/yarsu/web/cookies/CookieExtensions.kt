package ru.yarsu.web.cookies

import org.http4k.core.*
import org.http4k.core.cookie.*
import java.time.Instant

const val COOKIE_LIFETIME_SECONDS = 60 * 60 * 24L * 7 // secs in min * mins in hour * hours in day * days
const val SIXTY_SECONDS = 60L

fun Response.globalCookie(
    name: String,
    value: String,
): Response =
    this.cookie(
        Cookie(name, value, path = "/")
            .expires(Instant.now().plusSeconds(COOKIE_LIFETIME_SECONDS))
            .httpOnly()
            .sameSite(SameSite.Strict)
    )

fun Response.clearGlobalCookie(name: String): Response = this.cookie(Cookie(name, "", path = "/", maxAge = 0))

fun Response.clearCookie(
    name: String,
    path: String,
): Response = this.cookie(Cookie(name, "", path = path, maxAge = 0))

fun Response.withSmallLifetimeCookie(
    name: String,
    value: String,
): Response =
    this.cookie(
        Cookie(
            name = name,
            value = value,
            maxAge = SIXTY_SECONDS,
        )
            .httpOnly()
            .sameSite(SameSite.Strict)
    )
