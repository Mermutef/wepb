package ru.yarsu.web.auth.lenses

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.lens.BiDiMapping
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.map
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import org.http4k.lens.string
import ru.yarsu.domain.models.User
import ru.yarsu.domain.models.User.Companion.MAX_EMAIL_LENGTH
import ru.yarsu.domain.models.User.Companion.MAX_LOGIN_LENGTH
import ru.yarsu.domain.models.User.Companion.MAX_NAME_LENGTH
import ru.yarsu.domain.models.User.Companion.MAX_PHONE_NUMBER_LENGTH
import ru.yarsu.domain.models.User.Companion.MAX_SURNAME_LENGTH
import ru.yarsu.domain.models.User.Companion.MAX_VK_LINK_LENGTH
import ru.yarsu.domain.models.User.Companion.emailPattern
import ru.yarsu.domain.models.User.Companion.loginPattern
import ru.yarsu.domain.models.User.Companion.namePattern
import ru.yarsu.domain.models.User.Companion.phonePattern
import ru.yarsu.domain.models.User.Companion.vkLinkPattern
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.auth.handlers.SignInError
import ru.yarsu.web.auth.handlers.SignUpError
import ru.yarsu.web.lenses.GeneralWebLenses.idFromPathField
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull
import ru.yarsu.web.lenses.GeneralWebLenses.makeBodyLensForFields

object UserWebLenses {
    val nameFromPathLens = Path.string().of("name")

    private val passwordFieldTemplate = FormField
        .nonEmptyString()
        .nonBlankString()

    val passwordSignInField = passwordFieldTemplate.required(
        "password",
        SignInError.PASSWORD_IS_BLANK_OR_EMPTY.errorText
    )

    val passwordSignUpField = passwordFieldTemplate.required(
        "password",
        SignUpError.PASSWORD_IS_BLANK_OR_EMPTY.errorText
    )

    val nameField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { name: String ->
                    name.takeIf {
                        name.length in 1..MAX_NAME_LENGTH && namePattern.matches(name)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("name", UserLensErrors.NAME_NOT_CORRECT.errorText)

    val surnameField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { surname: String ->
                    surname.takeIf {
                        surname.length in 1..MAX_NAME_LENGTH && namePattern.matches(surname)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("surname", UserLensErrors.SURNAME_NOT_CORRECT.errorText)

    val loginField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { login: String ->
                    login.takeIf {
                        login.length in 1..MAX_LOGIN_LENGTH && loginPattern.matches(login)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("login", UserLensErrors.LOGIN_NOT_CORRECT.errorText)

    val phoneNumberField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { phone: String ->
                    phone.takeIf {
                        phone.length in 1..MAX_LOGIN_LENGTH && phonePattern.matches(phone.filter { it.isDigit() })
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("phoneNumber", UserLensErrors.PHONE_NOT_CORRECT.errorText)

    val emailField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { email: String ->
                    (
                        email.takeIf {
                            email.length in 1..MAX_EMAIL_LENGTH &&
                                emailPattern.matches(email)
                        }
                            ?: throw IllegalArgumentException("")
                    ).takeIf {
                        emailPattern.matches(email)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("email", UserLensErrors.EMAIL_NOT_CORRECT.errorText)

    val vkLinkField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { vkLink: String ->
                    vkLink.takeIf {
                        vkLink.length in 1..MAX_LOGIN_LENGTH && vkLinkPattern.matches(vkLink)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).optional("vk_link", UserLensErrors.VKLINK_NOT_CORRECT.errorText)

    val repeatPasswordField = FormField.nonEmptyString().nonBlankString()
        .required("repeat_password", SignUpError.REPEAT_PASSWORD_IS_BLANK_OR_EMPTY.errorText)

    val specialSignInField = FormField.nonEmptyString().nonBlankString()
        .required("login_or_phone_or_email", SignInError.SIGN_IN_DATA_IS_BLANK_OR_EMPTY.errorText)

    val signUpLens = makeBodyLensForFields(
        nameField,
        surnameField,
        loginField,
        phoneNumberField,
        emailField,
        passwordSignUpField,
        repeatPasswordField,
        vkLinkField
    )

    val sigInLens = makeBodyLensForFields(
        specialSignInField,
        passwordSignInField,
    )

    fun Request.extractUser(userOperations: UserOperationsHolder): Result<User, UserFetchingError> {
        return lensOrNull(idFromPathField, this)?.let {
            when (val fetchUserResult = userOperations.fetchUserByID(it)) {
                is Failure -> Failure(fetchUserResult.reason)
                is Success -> Success(fetchUserResult.value)
            }
        } ?: Failure(UserFetchingError.NO_SUCH_USER)
    }

    fun Request.authorizeUserFromPath(userLens: RequestContextLens<User?>): Result<User, UserFetchingError> {
        return lensOrNull(nameFromPathLens, this)?.let { userNameFromPath ->
            val authUser = userLens(this)
            when {
                authUser is User && authUser.name == userNameFromPath -> Success(authUser)
                else -> Failure(UserFetchingError.NO_SUCH_USER)
            }
        } ?: Failure(UserFetchingError.NO_SUCH_USER)
    }
}

enum class UserLensErrors(val errorText: String) {
    LOGIN_NOT_CORRECT(
        "Логин должен быть быть не пустым, иметь длину менее $MAX_LOGIN_LENGTH символов " +
            "и содержать только латинские бувы, цифры, знаки \"_\", \".\" и \"-\""
    ),
    EMAIL_NOT_CORRECT(
        "Электронная почта должна быть не пустой, иметь длину менее $MAX_EMAIL_LENGTH символов " +
            "и содержать только латинские бувы, цифры, знаки \"_\", \".\" и \"-\""
    ),
    NAME_NOT_CORRECT(
        "Имя должно быть не пустым, иметь длину менее $MAX_NAME_LENGTH символов " +
            "и содержать только кириллические бувы и знак \"-\""
    ),
    SURNAME_NOT_CORRECT(
        "Фамилия должна быть не пустой, иметь длину менее $MAX_SURNAME_LENGTH символов " +
            "и содержать только кириллические бувы и знак \"-\""
    ),
    PHONE_NOT_CORRECT(
        "Необходимо ввести корреткный номер телефона, номер телефона должен иметь длину " +
            "$MAX_PHONE_NUMBER_LENGTH и начинаться с цифры \"7\""
    ),
    VKLINK_NOT_CORRECT(
        "Необходимо ввести корреткную ссылку на vk-страницу, длина которой менне $MAX_VK_LINK_LENGTH"
    ),
}
