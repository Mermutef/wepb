package ru.yarsu.web.auth.models

import com.fasterxml.jackson.databind.type.LogicalType
import org.http4k.template.ViewModel
import ru.yarsu.web.form.CustomWebForm

class SignInVM(
    val form: CustomWebForm? = null,
    val loginType: LoginType = LoginType.LOGIN
) : ViewModel {
    enum class LoginType {LOGIN, EMAIL, PHONE}
}
