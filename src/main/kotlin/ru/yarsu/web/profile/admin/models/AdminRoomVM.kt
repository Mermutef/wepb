package ru.yarsu.web.profile.admin.models


import dev.forkhandles.result4k.Result
import org.http4k.template.ViewModel
import ru.yarsu.domain.models.User
import ru.yarsu.web.profile.admin.handlers.FetchingUsersError

class AdminRoomVM(
    val users: List<User>
) : ViewModel
