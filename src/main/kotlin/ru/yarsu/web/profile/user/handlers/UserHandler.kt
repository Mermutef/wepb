package ru.yarsu.web.profile.user.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import ru.yarsu.domain.models.User
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.auth.lenses.UserWebLenses.emailField
import ru.yarsu.web.auth.lenses.UserWebLenses.nameField
import ru.yarsu.web.auth.lenses.UserWebLenses.phoneNumberField
import ru.yarsu.web.auth.lenses.UserWebLenses.surnameField
import ru.yarsu.web.auth.lenses.UserWebLenses.vkLinkField
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.form.toCustomForm
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.user.models.UserRoomVM

class UserHandler(
    private val render: ContextAwareViewRender,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        when (
            val user = request.authorizeUserFromPath(
                userLens = userLens,
            )
        ) {
            is Failure -> notFound
            is Success -> {
                render(request) extract UserRoomVM(
                    form = WebForm()
                        .with(
                            nameField of user.value.name,
                            surnameField of user.value.surname,
                            emailField of user.value.email,
                            phoneNumberField of user.value.phoneNumber,
                            vkLinkField of user.value.vkLink,
                        )
                        .toCustomForm()
                )
            }
        }
}
