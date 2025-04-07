package ru.yarsu.web.auth.models

import org.http4k.template.ViewModel
import ru.yarsu.web.form.CustomWebForm

class SignUpVM(
    val form: CustomWebForm? = null,
) : ViewModel
