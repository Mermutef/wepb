package ru.yarsu.web.profile.admin.models


import dev.forkhandles.result4k.Result
import org.http4k.template.ViewModel
import ru.yarsu.domain.models.User
import ru.yarsu.web.profile.admin.handlers.ChangeRoleError
import ru.yarsu.web.profile.admin.handlers.FetchingUsersError

class AdminRoomVM(
    val users: List<User>,
    val error: ChangeRoleError? = null,
    val changedUserId: Int? = null
) : ViewModel
