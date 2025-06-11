package ru.yarsu.web.profile.admin.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User

class ManageUsersVM (
    val users: List<User>,
    val roles: List<Role>,
    val error: Pair<Int?, String?>? = null,
) : ViewModel
