package ru.yarsu.web.auth.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.failureOrNull
import org.http4k.core.*
import org.http4k.lens.WebForm
import ru.yarsu.domain.accounts.JWTTools
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.users.UserCreationError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.auth.lenses.UserWebLenses
import ru.yarsu.web.auth.models.SignUpVM
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.cookies.globalCookie
import ru.yarsu.web.extract
import ru.yarsu.web.form.addFailure
import ru.yarsu.web.form.toCustomForm
import ru.yarsu.web.redirect

class SignUpHandler(
    private val render: ContextAwareViewRender,
    private val userOperations: UserOperationsHolder,
    private val jwtTools: JWTTools,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val form = UserWebLenses.signUpForm(request)
        return if (form.errors.isNotEmpty()) {
            render(request) extract SignUpVM(form.toCustomForm())
        } else {
            when (val userInsertResult = tryInsert(form = form, userOperations = userOperations)) {
                is Failure -> {
                    render(request) extract SignUpVM(
                        form = form.toCustomForm().addFailure(
                            name = "no-specific",
                            description = userInsertResult.reason.errorText
                        ),
                    )
                }

                is Success -> {
                    when (val tokenResult = jwtTools.createUserJwt(userInsertResult.value.id)) {
                        is Failure -> {
                            render(request) extract SignUpVM(
                                form = form.toCustomForm().addFailure(
                                    name = "no-specific",
                                    description = SignUpError.TOKEN_CREATION_ERROR.errorText
                                ),
                            )
                        }

                        is Success -> redirect()
                            .globalCookie("auth", tokenResult.value)
                    }
                }
            }
        }
    }

    private fun tryInsert(
        form: WebForm,
        userOperations: UserOperationsHolder,
    ): Result<User, SignUpError> {
        val password = UserWebLenses.passwordSignUpField(form)
        val repeatPassword = UserWebLenses.repeatPasswordField(form)
        if (repeatPassword != password) return Failure(SignUpError.PASSWORDS_DO_NOT_MATCH)
        val login = UserWebLenses.loginField(form)
        val email = UserWebLenses.emailField(form)
        val name = UserWebLenses.nameField(form)
        val surname = UserWebLenses.surnameField(form)
        val phoneNumber = UserWebLenses.phoneNumberField(form)
        val vkLink = UserWebLenses.vkLinkField(form)
        return when (
            val result = userOperations
                .createUser(name, surname, login, email, phoneNumber, password, vkLink, Role.READER)
        ) {
            is Success -> Success(result.value)
            is Failure -> Failure(
                when (result.failureOrNull()) {
                    UserCreationError.LOGIN_ALREADY_EXISTS -> SignUpError.LOGIN_ALREADY_EXISTS
                    UserCreationError.INVALID_USER_DATA -> SignUpError.INVALID_USER_DATA
                    UserCreationError.EMAIL_ALREADY_EXISTS -> SignUpError.EMAIL_ALREADY_EXISTS
                    UserCreationError.PHONE_ALREADY_EXISTS -> SignUpError.PHONE_ALREADY_EXISTS
                    else -> SignUpError.UNKNOWN_DATABASE_ERROR
                }
            )
        }
    }
}

enum class SignUpError(val errorText: String) {
    PASSWORD_IS_BLANK_OR_EMPTY("Пароль должен быть не пустым"),
    REPEAT_PASSWORD_IS_BLANK_OR_EMPTY("Повтор пароля не может быть пустым"),
    PASSWORDS_DO_NOT_MATCH("Пароли должны совпадать"),
    LOGIN_ALREADY_EXISTS("Имя пользователя уже занято"),
    PHONE_ALREADY_EXISTS("Номер телефона уже занят"),
    EMAIL_ALREADY_EXISTS("Адрес электронной почты уже занят"),
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    TOKEN_CREATION_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    INVALID_USER_DATA("Неверные данные пользователя"),
}
