package ru.yarsu.web.profile.admin.models


import org.http4k.template.ViewModel
import ru.yarsu.domain.models.User

class AdminRoomVM(
    val users: Map<Int, User>
) : ViewModel
