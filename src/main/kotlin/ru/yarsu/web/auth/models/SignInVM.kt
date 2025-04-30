package ru.yarsu.web.auth.models

import org.http4k.template.ViewModel
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.auth.SIGN_UP
import ru.yarsu.web.form.CustomWebForm

class SignInVM(
    val form: CustomWebForm? = null,
) : ViewModel {
    val signUpLink = "$AUTH_SEGMENT$SIGN_UP"
}
