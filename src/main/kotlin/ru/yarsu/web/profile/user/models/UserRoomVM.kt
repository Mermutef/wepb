package ru.yarsu.web.profile.user.models

import org.http4k.template.ViewModel
import ru.yarsu.web.form.CustomWebForm

class UserRoomVM(
    val form: CustomWebForm? = null,
    val showPasswordModal: Boolean = false,
    val showNameModal: Boolean = false,
    val showSurnameModal: Boolean = false,
    val showEmailModal: Boolean = false,
    val showPhoneModal: Boolean = false,
    val showVKModal: Boolean = false,
) : ViewModel
