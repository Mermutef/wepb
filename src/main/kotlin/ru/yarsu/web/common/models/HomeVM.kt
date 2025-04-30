package ru.yarsu.web.common.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.User

class HomeVM(
    val user: User?,
) : ViewModel
