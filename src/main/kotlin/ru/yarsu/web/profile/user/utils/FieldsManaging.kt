package ru.yarsu.web.profile.user.utils

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.with
import org.http4k.lens.BiDiLens
import org.http4k.lens.WebForm
import ru.yarsu.config.AuthConfig
import ru.yarsu.domain.accounts.PasswordHasher
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.domain.operations.users.FieldInUserChangingError
import ru.yarsu.web.auth.lenses.UserWebLenses.emailField
import ru.yarsu.web.auth.lenses.UserWebLenses.nameField
import ru.yarsu.web.auth.lenses.UserWebLenses.oldPasswordField
import ru.yarsu.web.auth.lenses.UserWebLenses.passwordSignUpField
import ru.yarsu.web.auth.lenses.UserWebLenses.phoneNumberField
import ru.yarsu.web.auth.lenses.UserWebLenses.repeatPasswordField
import ru.yarsu.web.auth.lenses.UserWebLenses.surnameField
import ru.yarsu.web.auth.lenses.UserWebLenses.vkLinkField
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.form.CustomWebForm
import ru.yarsu.web.form.addFailure
import ru.yarsu.web.form.replaceOrAddFrom
import ru.yarsu.web.form.toCustomForm
import ru.yarsu.web.lenses.GeneralWebLenses.from
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull
import ru.yarsu.web.profile.user.USER
import ru.yarsu.web.profile.user.lenses.UserFieldsWebLenses.changeEmailForm
import ru.yarsu.web.profile.user.lenses.UserFieldsWebLenses.changeNameForm
import ru.yarsu.web.profile.user.lenses.UserFieldsWebLenses.changePasswordForm
import ru.yarsu.web.profile.user.lenses.UserFieldsWebLenses.changePhoneForm
import ru.yarsu.web.profile.user.lenses.UserFieldsWebLenses.changeSurnameForm
import ru.yarsu.web.profile.user.lenses.UserFieldsWebLenses.changeVKForm
import ru.yarsu.web.profile.user.models.UserRoomVM
import ru.yarsu.web.redirect

fun User.defaultForm(): CustomWebForm =
    WebForm().with(
        nameField of this.name,
        surnameField of this.surname,
        emailField of this.email,
        phoneNumberField of this.phoneNumber,
        vkLinkField of this.vkLink,
    ).toCustomForm()

fun OperationsHolder.changeField(
    request: Request,
    render: ContextAwareViewRender,
    user: User,
    field: UserField,
    config: AuthConfig,
): Response {
    return when (field) {
        UserField.NAME -> changeStringField(user, changeNameForm from request, nameField, userOperations.changeName)
        UserField.SURNAME -> changeStringField(
            user,
            changeSurnameForm from request,
            surnameField,
            userOperations.changeSurname
        )

        UserField.EMAIL -> changeStringField(user, changeEmailForm from request, emailField, userOperations.changeEmail)
        UserField.PHONE_NUMBER -> changeStringField(
            user,
            changePhoneForm from request,
            phoneNumberField,
            userOperations.changePhoneNumber
        )

        UserField.PASSWORD -> changePassword(user, changePasswordForm from request, config)
        UserField.VK_LINK -> changeVKLink(
            user,
            changeVKForm from request,
        )
    }?.let {
        render(request) extract UserRoomVM(
            user = user,
            form = user.defaultForm() replaceOrAddFrom it,
            fieldType = field
        )
    } ?: redirect("$USER/${user.login}")
}

fun OperationsHolder.changePassword(
    user: User,
    form: WebForm,
    config: AuthConfig,
): CustomWebForm? {
    if (form.errors.isNotEmpty()) {
        return form.toCustomForm()
    }

    val oldPassword = oldPasswordField from form
    val newPassword = passwordSignUpField from form
    val repeatNewPassword = repeatPasswordField from form

    return when {
        user.password != PasswordHasher(config).hash(oldPassword) ->
            form.toCustomForm().addFailure(
                name = oldPasswordField.meta.name,
                description = ChangeFieldsError.INCORRECT_OLD_PASSWORD.errorText,
            )

        newPassword != repeatNewPassword ->
            form.toCustomForm().addFailure(
                name = repeatPasswordField.meta.name,
                description = ChangeFieldsError.PASSWORDS_DO_NOT_MATCH.errorText,
            )

        else -> {
            this.userOperations.changePassword(user, newPassword).let {
                when (it) {
                    is Success -> null
                    is Failure -> {
                        form.toCustomForm().addFailure(
                            name = "no-specific",
                            description = ChangeFieldsError.UNKNOWN_DATABASE_ERROR.errorText,
                        )
                    }
                }
            }
        }
    }
}

fun changeStringField(
    user: User,
    form: WebForm,
    field: BiDiLens<WebForm, String>,
    change: (User, String) -> Result<User, FieldInUserChangingError>,
): CustomWebForm? {
    if (form.errors.isNotEmpty()) {
        return form.toCustomForm()
    }

    return change(user, field from form).let {
        when (it) {
            is Success -> null
            is Failure -> {
                form.toCustomForm().addFailure(
                    name = "no-specific",
                    description = ChangeFieldsError.UNKNOWN_DATABASE_ERROR.errorText,
                )
            }
        }
    }
}

fun OperationsHolder.changeVKLink(
    user: User,
    form: WebForm,
): CustomWebForm? {
    if (form.errors.isNotEmpty()) {
        return form.toCustomForm()
    }

    return lensOrNull(vkLinkField, form)?.let { newVkLink ->
        userOperations.changeVKLink(user, newVkLink).let {
            when (it) {
                is Success -> null
                is Failure -> {
                    form.toCustomForm().addFailure(
                        name = "no-specific",
                        description = ChangeFieldsError.UNKNOWN_DATABASE_ERROR.errorText,
                    )
                }
            }
        }
    }
}

enum class UserField {
    NAME,
    SURNAME,
    EMAIL,
    PASSWORD,
    PHONE_NUMBER,
    VK_LINK,
}

fun UserField.toPathString() = this.toString().lowercase().replace("_", "-")

enum class ChangeFieldsError(val errorText: String) {
    PASSWORDS_DO_NOT_MATCH("Пароли должны совпадать"),
    INCORRECT_OLD_PASSWORD("Неверный пароль"),
    UNKNOWN_DATABASE_ERROR("Что-то пошло не так. Повторите попытку позднее"),
}
