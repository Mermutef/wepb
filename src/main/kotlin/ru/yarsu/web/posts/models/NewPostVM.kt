package ru.yarsu.web.posts.models

import org.http4k.template.ViewModel
import ru.yarsu.web.form.CustomWebForm

class NewPostVM(
    val form: CustomWebForm? = null,
) : ViewModel
