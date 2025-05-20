package ru.yarsu.web.profile.user.lenses

import org.http4k.lens.Path
import org.http4k.lens.string
import ru.yarsu.web.auth.lenses.UserWebLenses.emailField
import ru.yarsu.web.auth.lenses.UserWebLenses.nameField
import ru.yarsu.web.auth.lenses.UserWebLenses.oldPasswordField
import ru.yarsu.web.auth.lenses.UserWebLenses.passwordSignUpField
import ru.yarsu.web.auth.lenses.UserWebLenses.phoneNumberField
import ru.yarsu.web.auth.lenses.UserWebLenses.repeatPasswordField
import ru.yarsu.web.auth.lenses.UserWebLenses.surnameField
import ru.yarsu.web.auth.lenses.UserWebLenses.vkLinkField
import ru.yarsu.web.lenses.GeneralWebLenses.makeBodyLensForFields
import ru.yarsu.web.profile.user.utils.UserField
import ru.yarsu.web.profile.user.utils.toPathString

object UserFieldsWebLenses {
    val userFieldNameFromPath =
        Path.string()
            .map(
                nextIn = {
                    runCatching {
                        UserField.valueOf(it.uppercase().replace("-", "_"))
                    }.getOrNull() ?: throw IllegalArgumentException("")
                },
                nextOut = { it.toPathString() }
            ).of("field")
    val changePasswordForm = makeBodyLensForFields(
        passwordSignUpField,
        repeatPasswordField,
        oldPasswordField,
    )
    val changeNameForm = makeBodyLensForFields(nameField)
    val changeSurnameForm = makeBodyLensForFields(surnameField)
    val changeVKForm = makeBodyLensForFields(vkLinkField)
    val changeEmailForm = makeBodyLensForFields(emailField)
    val changePhoneForm = makeBodyLensForFields(phoneNumberField)
}
