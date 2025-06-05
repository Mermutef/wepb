package ru.yarsu.web.errors.models

import org.http4k.template.ViewModel

class NotFoundVM(
    val path: String,
) : ViewModel
