package ru.yarsu.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.models.User.Companion.MAX_EMAIL_LENGTH
import ru.yarsu.domain.models.User.Companion.MAX_NAME_LENGTH
import ru.yarsu.domain.models.User.Companion.validateUserData

class UserTest : FunSpec({
    val validUsers = listOf(
        Triple(
            "Valid_Name-1.0",
            "Valid_e-mail-1.0@mail_Host-1.com",
            "pass",
        ),
        Triple(
            "a".repeat(MAX_NAME_LENGTH),
            "b".repeat(MAX_EMAIL_LENGTH - 9) + "@anfas.ru",
            "pass",
        ),
        Triple(
            "Valid_Name-1.0",
            "some@email.com",
            "pass",
        ),
        Triple(
            "some-name",
            "Valid_e-mail-1.0@mail_Host-1.com",
            "pass",
        ),
    )

    val nameWithInvalidSymbols = Triple(
        "In\$valid_Name-1.0",
        "Valid_e-mail-1.0@mail_Host-1.com",
        "pass",
    )

    val emailWithInvalidSymbols = Triple(
        "some-name",
        "Invalid_e-mail-1.0@mail_Host-1.ru.com",
        "pass",
    )

    val tooLongEmail = Triple(
        "some-username",
        "b".repeat(MAX_EMAIL_LENGTH) + "@anfas.ru",
        "pass",
    )

    val tooLongName = Triple(
        "a".repeat(MAX_NAME_LENGTH + 1),
        "some@email.com",
        "pass",
    )

    val nameWithCyrillicSymbols = Triple(
        "Кирилличекое-имя",
        "some@email.com",
        "pass",
    )

    val nameWithSpaces = Triple(
        "Name with spaces",
        "Valid_e-mail-1.0@mail_Host-1.com",
        "pass",
    )

    validUsers.forEach { userdata ->
        test("Should return UserValidationResult.ALL_OK when userdata is $userdata") {
            validateUserData(
                userdata.first,
                userdata.second,
                userdata.third,
            ) shouldBe UserValidationResult.ALL_OK
        }
    }

    listOf(
        nameWithSpaces,
        nameWithCyrillicSymbols,
        nameWithInvalidSymbols,
    ).forEach { userdata ->
        test("Should return UserValidationResult.NAME_PATTERN_MISMATCH when userdata is $userdata") {
            validateUserData(
                userdata.first,
                userdata.second,
                userdata.third,
            ) shouldBe UserValidationResult.NAME_PATTERN_MISMATCH
        }
    }

    test("Should return UserValidationResult.EMAIL_PATTERN_MISMATCH when userdata is $emailWithInvalidSymbols") {
        validateUserData(
            emailWithInvalidSymbols.first,
            emailWithInvalidSymbols.second,
            emailWithInvalidSymbols.third,
        ) shouldBe UserValidationResult.EMAIL_PATTERN_MISMATCH
    }

    test("Should return UserValidationResult.EMAIL_IS_TOO_LONG when userdata is $tooLongEmail") {
        validateUserData(
            tooLongEmail.first,
            tooLongEmail.second,
            tooLongEmail.third,
        ) shouldBe UserValidationResult.EMAIL_IS_TOO_LONG
    }

    test("Should return UserValidationResult.NAME_IS_TOO_LONG when userdata is $tooLongName") {
        validateUserData(
            tooLongName.first,
            tooLongName.second,
            tooLongName.third,
        ) shouldBe UserValidationResult.NAME_IS_TOO_LONG
    }
})
