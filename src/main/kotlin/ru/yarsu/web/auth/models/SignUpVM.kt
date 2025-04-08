package ru.yarsu.web.auth.models

import org.http4k.template.ViewModel
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.auth.SIGN_IN
import ru.yarsu.web.form.CustomWebForm

class SignUpVM(
    val form: CustomWebForm? = null,
) : ViewModel {
    val signInLink = "$AUTH_SEGMENT$SIGN_IN"
}
