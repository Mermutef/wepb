package ru.yarsu.web.profile

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.filter.ServerFilters
import org.http4k.lens.BiDiBodyLens
import org.http4k.template.ViewModel
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.yarsu.config.AuthConfig
import ru.yarsu.config.WebConfig
import ru.yarsu.domain.accounts.JWTTools
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.auth.handlers.SignInHandler
import ru.yarsu.web.auth.models.SignInVM
import ru.yarsu.web.context.ContextTools
import ru.yarsu.web.context.templates.ContextAwareViewRender

class SignInHandlerTest : StringSpec({
    val testWebConfig = WebConfig(
        port = 8080,
        domain = "test.local",
        hotReloadTemplates = true
    )

    val tools = ContextTools(config = testWebConfig)

    lateinit var handler: HttpHandler
    lateinit var jwtTools: JWTTools
    val validPass = "pass"

    val testAuthConfig = AuthConfig(
        salt = "test-salt-value",
        secret = "test-secret-key",
        generalPassword = "admin123"
    )

    val testWriter = User(
        id = 2,
        name = "Сева",
        surname = "Порзлов",
        login = "folqde",
        password = PasswordHasher(testAuthConfig).hash(validPass),
        email = "ybnvd@js.ru",
        phoneNumber = "79065264411",
        vkLink = null,
        role = Role.WRITER
    )

    val testReader = User(
        id = 1,
        name = "Иван",
        surname = "Иванов",
        login = "writer",
        password = PasswordHasher(testAuthConfig).hash(validPass),
        email = "writer@test.com",
        phoneNumber = "79001234567",
        vkLink = "https://vk.com/writer",
        role = Role.READER
    )

    val testModerator = User(
        id = 1,
        name = "Иван",
        surname = "Иванов",
        login = "writer",
        password = PasswordHasher(testAuthConfig).hash(validPass),
        email = "writer@test.com",
        phoneNumber = "79001234567",
        vkLink = "https://vk.com/writer",
        role = Role.MODERATOR
    )

    val userOperationsMock: UserOperationsHolder = mock()

    whenever(userOperationsMock.fetchUserByLogin).thenReturn(mock())
    whenever(userOperationsMock.fetchUserByEmail).thenReturn(mock())
    whenever(userOperationsMock.fetchUserByPhone).thenReturn(mock())

    val validToken = "some-token"

    lateinit var renderMock: ContextAwareViewRender
    lateinit var lensMock: BiDiBodyLens<ViewModel>

    beforeEach {
        renderMock = mock()
        lensMock = mock()
        whenever(renderMock.invoke(any())).thenReturn(lensMock)
        whenever(lensMock.of<Response>(any())).thenReturn { it }

        jwtTools = mock()

        handler =
            ServerFilters
                .InitialiseRequestContext(tools.appContexts)
                .then(
                    SignInHandler(
                        render = renderMock,
                        userOperations = userOperationsMock,
                        config = testAuthConfig,
                        jwtTools = jwtTools,
                    ),
                )
    }

    "Должен перенаправлять на главную страницу при успешной аутентификации ЧИТАТЕЛЯ" {
        val request =
            Request(Method.POST, "/auth/sign-in")
                .form("login_or_phone_or_email", testReader.login)
                .form("password", validPass)
                .header("content-type", "application/x-www-form-urlencoded")

        whenever(userOperationsMock.fetchUserByLogin(testReader.login)).thenReturn(Success(testReader))

        whenever(jwtTools.createUserJwt(testReader.id)).thenReturn(Success(validToken))

        val response = handler(request)

        response.status shouldBe org.http4k.core.Status.FOUND
        response.header("Location") shouldBe "/"
    }

    "Должен перенаправлять на главную страницу при успешной аутентификации ПИСАТЕЛЯ" {
        val request =
            Request(Method.POST, "/auth/sign-in")
                .form("login_or_phone_or_email", testWriter.login)
                .form("password", validPass)
                .header("content-type", "application/x-www-form-urlencoded")

        whenever(userOperationsMock.fetchUserByLogin(testWriter.login)).thenReturn(Success(testWriter))

        whenever(jwtTools.createUserJwt(testWriter.id)).thenReturn(Success(validToken))

        val response = handler(request)

        response.status shouldBe org.http4k.core.Status.FOUND
        response.header("Location") shouldBe "/"
    }

    "Должен перенаправлять на главную страницу при успешной аутентификации МОДЕРАТОРА" {
        val request =
            Request(Method.POST, "/auth/sign-in")
                .form("login_or_phone_or_email", testModerator.login)
                .form("password", validPass)
                .header("content-type", "application/x-www-form-urlencoded")

        whenever(userOperationsMock.fetchUserByLogin(testModerator.login)).thenReturn(Success(testModerator))

        whenever(jwtTools.createUserJwt(testModerator.id)).thenReturn(Success(validToken))

        val response = handler(request)

        response.status shouldBe org.http4k.core.Status.FOUND
        response.header("Location") shouldBe "/"
    }

    "Должен возвращать непустую форму при неверных учетных данных ЛЮБЫХ" {
        whenever(userOperationsMock.fetchUserByLogin(any())).thenReturn(Failure(UserFetchingError.NO_SUCH_USER))

        val request =
            Request(Method.POST, "/auth/sign-in")
                .form("login_or_phone_or_email", "invalid_login")
                .form("password", "wrong_password")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe org.http4k.core.Status.OK
        verify(lensMock).of<Response>(
            check<SignInVM> { model ->
                model.form shouldNotBe null
            },
        )
    }

    "Должен возвращать непустую форму при пустых учетных данных" {
        whenever(userOperationsMock.fetchUserByLogin(any())).thenReturn(Failure(UserFetchingError.NO_SUCH_USER))

        val request =
            Request(Method.POST, "/auth/sign-in")
                .form("login_or_phone_or_email", "")
                .form("password", "")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe org.http4k.core.Status.OK
        verify(lensMock).of<Response>(
            check<SignInVM> { model ->
                model.form shouldNotBe null
            },
        )
    }
})
