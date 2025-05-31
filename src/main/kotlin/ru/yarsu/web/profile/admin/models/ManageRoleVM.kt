package ru.yarsu.web.profile.admin.models

import dev.forkhandles.result4k.Success
import org.http4k.template.ViewModel
import ru.yarsu.domain.models.User
import ru.yarsu.web.profile.admin.handlers.ChangeRoleError

class ManageRoleVM (
    val users: List<User>,
    val success : Boolean,
    val error: ChangeRoleError?

): ViewModel
