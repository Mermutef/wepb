package ru.yarsu.web.auth.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.lens.WebForm
import ru.yarsu.config.AuthConfig
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.models.User
import ru.yarsu.domain.models.User.Companion.MAX_NAME_LENGTH
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.domain.tools.JWTTools
import ru.yarsu.web.auth.models.SignInVM
import ru.yarsu.web.cookies.globalCookie
import ru.yarsu.web.form.addFailure
import ru.yarsu.web.form.toCustomForm
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.lenses.UserWebLenses
import ru.yarsu.web.redirect

class SignInHandler(
    private val render: ContextAwareViewRender,
    private val userOperations: UserOperationsHolder,
    private val config: AuthConfig,
    private val jwtTools: JWTTools,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val form = UserWebLenses.sigInLens(request)
        return if (form.errors.isNotEmpty()) {
            render(request) extract SignInVM(form.toCustomForm())
        } else {
            when (val signInResult = checkNamePass(form = form, userOperations = userOperations, config = config)) {
                is Failure -> {
                    render(request) extract SignInVM(
                        form = form.toCustomForm().addFailure(
                            name = signInResult.reason.toString(),
                            description = signInResult.reason.errorText
                        )
                    )
                }

                is Success -> {
                    when (val tokenResult = jwtTools.createUserJwt(signInResult.value.id)) {
                        is Failure -> {
                            render(request) extract SignInVM(
                                form = form.toCustomForm().addFailure(
                                    name = SignInError.TOKEN_CREATION_ERROR.toString(),
                                    description = SignInError.TOKEN_CREATION_ERROR.errorText,
                                )
                            )
                        }

                        is Success -> redirect("/")
                            .globalCookie("auth", tokenResult.value)
                    }
                }
            }
        }
    }

    private fun checkNamePass(
        form: WebForm,
        userOperations: UserOperationsHolder,
        config: AuthConfig,
    ): Result<User, SignInError> {
        val login = UserWebLenses.nameField(form)
        val password = UserWebLenses.passwordSignInField(form)
        return when (val result = userOperations.fetchUserByName(login)) {
            is Failure -> when (result.reason) {
                UserFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(SignInError.UNKNOWN_DATABASE_ERROR)
                UserFetchingError.NO_SUCH_USER -> Failure(SignInError.INCORRECT_NAME_OR_PASS)
            }

            is Success -> if (result.value.pass == PasswordHasher(config).hash(password)) {
                Success(result.value)
            } else {
                Failure(SignInError.INCORRECT_NAME_OR_PASS)
            }
        }
    }
}

enum class SignInError(val errorText: String) {
    NAME_IS_BLANK_OR_EMPTY("Имя пользователя должно быть не пустым"),
    PASSWORD_IS_BLANK_OR_EMPTY("Пароль должен быть не пустым"),
    NAME_IS_TOO_LONG("Имя пользователя должно быть короче $MAX_NAME_LENGTH символов"),
    INCORRECT_NAME_OR_PASS("Неверный логин или пароль"),
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    TOKEN_CREATION_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
}
