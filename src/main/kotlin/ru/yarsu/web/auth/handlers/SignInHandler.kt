package ru.yarsu.web.auth.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.lens.WebForm
import ru.yarsu.config.AuthConfig
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.models.User
import ru.yarsu.domain.models.User.Companion.MAX_LOGIN_LENGTH
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.domain.tools.JWTTools
import ru.yarsu.web.auth.lenses.UserWebLenses
import ru.yarsu.web.auth.models.SignInVM
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.cookies.globalCookie
import ru.yarsu.web.extract
import ru.yarsu.web.form.addFailure
import ru.yarsu.web.form.toCustomForm
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
        // todo наврала, вроде аутентификация пока только по логину, а надо еще
        // по номеру телефона и почте (необходимо создать функцию в бд - fetch по номеру телефона, по почте уже есть)
        return when (val result = userOperations.fetchUserByName(login)) { // поиск юзера с данным логином
            is Failure -> when (result.reason) {
                UserFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(SignInError.UNKNOWN_DATABASE_ERROR)
                UserFetchingError.NO_SUCH_USER -> Failure(SignInError.INCORRECT_LOGIN_OR_PASS)
            }

            is Success -> if (result.value.password == PasswordHasher(config).hash(password)) {
                Success(result.value)
            } else {
                Failure(SignInError.INCORRECT_LOGIN_OR_PASS)
            }
        }
    }
}
// todo здесь нужно внести возможные ошибки с текстом,
// текст будет показываться пользователю на страничке формы входа посмотри какие есть в SignUpHandler

enum class SignInError(val errorText: String) {
    LOGIN_IS_BLANK_OR_EMPTY("Имя пользователя должно быть не пустым"),
    PASSWORD_IS_BLANK_OR_EMPTY("Пароль должен быть не пустым"),
    LOGIN_IS_TOO_LONG("Имя пользователя должно быть короче $MAX_LOGIN_LENGTH символов"),
    INCORRECT_LOGIN_OR_PASS("Неверный логин или пароль"),
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    TOKEN_CREATION_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
}
