package ru.yarsu.web.lenses

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
import ru.yarsu.domain.models.User.Companion.MAX_NAME_LENGTH
import ru.yarsu.domain.models.User.Companion.emailPattern
import ru.yarsu.domain.models.User.Companion.namePattern
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.idFromPathField
import ru.yarsu.web.lenses.GeneralWebLenses.makeBodyLensForFields
import ru.yarsu.web.auth.handlers.SignInError
import ru.yarsu.web.auth.handlers.SignUpError
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull

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
                        name.length in 1..MAX_NAME_LENGTH &&
                                namePattern.matches(name)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("name", UserLensErrors.LOGIN_NOT_CORRECT.errorText)


    val surnameField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { surname: String ->
                    surname.takeIf {
                        surname.length in 1..MAX_NAME_LENGTH &&
                                namePattern.matches(surname)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("surname", UserLensErrors.LOGIN_NOT_CORRECT.errorText)

    val loginField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { login: String ->
                    login.takeIf {
                        login.length in 1..MAX_NAME_LENGTH &&
                            namePattern.matches(login)
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
                        phone.length in 1..MAX_NAME_LENGTH &&
                                namePattern.matches(phone)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("phoneNumber", UserLensErrors.LOGIN_NOT_CORRECT.errorText)

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
                        vkLink.length in 1..MAX_NAME_LENGTH &&
                                namePattern.matches(vkLink)
                    } ?: throw IllegalArgumentException("")
                },
                asIn = { it }
            )
        ).required("login", UserLensErrors.LOGIN_NOT_CORRECT.errorText)

    val repeatPasswordField = FormField.nonEmptyString().nonBlankString()
        .required("repeatPassword", SignUpError.REPEAT_PASSWORD_IS_BLANK_OR_EMPTY.errorText)

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
        loginField,
        passwordSignInField,
    )
    //todo здесь надо посмотреть что с менеджером!
    val setManagerLens = makeBodyLensForFields(loginField)

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

//todo внести необходимые тексты ошибок

enum class UserLensErrors(val errorText: String) {
    LOGIN_NOT_CORRECT(
        "Имя пользователя должно быть не пустым, иметь длину менее $MAX_NAME_LENGTH символов " +
            "и содержать только латинские бувы, цифры, знаки \"_\", \".\" и \"-\""
    ),
    EMAIL_NOT_CORRECT(
        "Электронная почта должна быть не пустой, иметь длину менее $MAX_EMAIL_LENGTH символов " +
            "и содержать только латинские бувы, цифры, знаки \"_\", \".\" и \"-\""
    ),
}
