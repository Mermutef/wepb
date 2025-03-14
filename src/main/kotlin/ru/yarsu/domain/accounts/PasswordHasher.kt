package ru.yarsu.domain.accounts

import ru.yarsu.config.AuthConfig
import java.security.MessageDigest
import java.util.HexFormat

class PasswordHasher (
    private val config: AuthConfig,
) {
    companion object {
        private val commaFormat = HexFormat.of()
        private var messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")

        private fun hashPassword(
            password: String,
            salt: String,
        ): String =
            commaFormat
                .formatHex(
                    messageDigest.digest(
                        (password + salt).toByteArray()
                    )
                )
    }

    fun hash(password: String) = hashPassword(password, config.salt)
}
