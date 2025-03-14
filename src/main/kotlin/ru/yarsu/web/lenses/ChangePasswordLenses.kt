package ru.yarsu.web.lenses

import org.http4k.lens.FormField
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import ru.yarsu.web.lenses.GeneralWebLenses.makeBodyLensForFields

object ChangePasswordLenses {
    private val passwordFieldTemplate =
        FormField
            .nonEmptyString()
            .nonBlankString()

    val oldPasswordField =
        passwordFieldTemplate
            .required(
                "old-password",
                "Password is blank or empty"
            )

    val newPasswordField =
        passwordFieldTemplate
            .required(
                "new-password",
                "Password is blank or empty"
            )

    val repeatNewPasswordField =
        passwordFieldTemplate
            .required(
                "repeat-new-password",
                "Repeat password is blank or empty"
            )

    val changePasswordForm = makeBodyLensForFields(
        oldPasswordField,
        newPasswordField,
        repeatNewPasswordField,
    )
}
