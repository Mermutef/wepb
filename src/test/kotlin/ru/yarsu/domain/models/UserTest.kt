package ru.yarsu.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validLogin
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validUserSurname
import ru.yarsu.domain.operations.validVKLink

class UserTest : FunSpec({

    test("blank name") {
        User.validateName("  \t\n") shouldBe UserValidationResult.NAME_IS_BLANK_OR_EMPTY
    }

    test("too long name") {
        User.validateName("a".repeat(User.MAX_NAME_LENGTH + 1)) shouldBe UserValidationResult.NAME_IS_TOO_LONG
    }

    test("not matching pattern name") {
        User.validateName("Vasy") shouldBe UserValidationResult.NAME_PATTERN_MISMATCH
    }

    test("valid name") {
        User.validateName("Вася") shouldBe null
    }

    test("blank surname") {
        User.validateSurname("  \t\n") shouldBe UserValidationResult.SURNAME_IS_BLANK_OR_EMPTY
    }

    test("too long surname") {
        User.validateSurname("a".repeat(User.MAX_SURNAME_LENGTH + 1)) shouldBe UserValidationResult
            .SURNAME_IS_TOO_LONG
    }

    test("not matching pattern surname") {
        User.validateSurname("Vasiliev") shouldBe UserValidationResult.SURNAME_PATTERN_MISMATCH
    }

    test("valid surname") {
        User.validateSurname("Васильев") shouldBe null
    }

    test("blank login") {
        User.validateLogin("  \t\n") shouldBe UserValidationResult.LOGIN_IS_BLANK_OR_EMPTY
    }

    test("too long login") {
        User.validateLogin("a".repeat(User.MAX_LOGIN_LENGTH + 1)) shouldBe UserValidationResult.LOGIN_IS_TOO_LONG
    }

    test("not matching pattern login") {
        User.validateLogin("@@@@") shouldBe UserValidationResult.LOGIN_PATTERN_MISMATCH
    }

    test("valid login") {
        User.validateLogin(validLogin) shouldBe null
    }

    test("blank email") {
        User.validateEmail("  \t\n") shouldBe UserValidationResult.EMAIL_IS_BLANK_OR_EMPTY
    }

    test("too long email") {
        User.validateEmail("a".repeat(User.MAX_EMAIL_LENGTH + 1)) shouldBe UserValidationResult.EMAIL_IS_TOO_LONG
    }

    test("not matching pattern email") {
        User.validateEmail("invalid") shouldBe UserValidationResult.EMAIL_PATTERN_MISMATCH
    }

    test("valid email") {
        User.validateEmail(validEmail) shouldBe null
    }

    test("blank phone") {
        User.validatePhoneNumber("  \t\n") shouldBe UserValidationResult.PHONE_NUMBER_IS_BLANK_OR_EMPTY
    }

    test("too long phone") {
        User.validatePhoneNumber("a".repeat(User.MAX_PHONE_NUMBER_LENGTH + 1)) shouldBe UserValidationResult
            .PHONE_NUMBER_IS_TOO_LONG
    }

    test("not matching pattern phone") {
        User.validatePhoneNumber("88888888888") shouldBe UserValidationResult.PHONE_NUMBER_PATTERN_MISMATCH
    }

    test("valid phone") {
        User.validatePassword(validPhoneNumber.filter { it.isDigit() }) shouldBe null
    }

    test("blank password") {
        User.validatePassword("  \t\n") shouldBe UserValidationResult.PASSWORD_IS_BLANK_OR_EMPTY
    }

    test("too long password") {
        User.validatePassword("a".repeat(User.MAX_PASSWORD_LENGTH + 1)) shouldBe UserValidationResult
            .PASSWORD_IS_TOO_LONG
    }

    test("valid password") {
        User.validatePassword(validPass) shouldBe null
    }

    test("blank vk link") {
        User.validateVKLink("  \t\n") shouldBe UserValidationResult.VK_LINK_IS_BLANK_OR_EMPTY
    }

    test("too long vk link") {
        User.validateVKLink("a".repeat(User.MAX_VK_LINK_LENGTH + 1)) shouldBe UserValidationResult.VK_LINK_IS_TOO_LONG
    }

    test("not matching pattern vk link") {
        User.validateVKLink("1$validVKLink") shouldBe UserValidationResult.VK_LINK_PATTERN_MISMATCH
    }

    test("valid vk link") {
        User.validateVKLink(validVKLink) shouldBe null
    }

    test("valid user") {
        val user = User(
            1,
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink,
            Role.ANONYMOUS
        )
        user.shouldNotBeNull()

        User.validateUserData(
            validName,
            validUserSurname,
            validLogin,
            validEmail,
            validPhoneNumber,
            validPass,
            validVKLink
        ) shouldBe UserValidationResult.ALL_OK
    }
})
