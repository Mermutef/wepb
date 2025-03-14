package ru.yarsu.domain.tools

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.domain.accounts.JWTTools
import ru.yarsu.domain.accounts.TokenError

class JWTToolsTest : FunSpec({
    val jwtTools = JWTTools("secret", "issuer")
    val userId = 1

    test("Create user JWT should return a valid JWT token") {
        jwtTools.createUserJwt(userId).shouldBeSuccess()
    }

    test("Verify valid JWT token should return the correct user ID") {
        val token = jwtTools.createUserJwt(userId)
            .shouldBeSuccess().toString()
        jwtTools.verifyToken(token).shouldBeSuccess()
    }

    test("Verify invalid JWT token should return a failure") {
        val invalidToken = "invalid_token"
        jwtTools.verifyToken(invalidToken)
            .shouldBeFailure(TokenError.DECODING_ERROR)
    }

    test("Verify expired JWT token should return a failure") {
        val expired = jwtTools.createUserJwt(userId, -1)
            .shouldBeSuccess()

        jwtTools.verifyToken(expired)
            .shouldBeFailure(TokenError.EXPIRED_TOKEN)
    }

    test("Invalid JWTTools creation") {
        JWTTools("", "issuer")
            .createUserJwt(userId)
            .shouldBeFailure(TokenError.CREATION_ERROR)
    }
})
