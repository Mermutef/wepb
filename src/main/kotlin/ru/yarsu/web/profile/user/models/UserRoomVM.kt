package ru.yarsu.web.profile.user.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.User
import ru.yarsu.web.form.CustomWebForm
import ru.yarsu.web.profile.user.USER
import ru.yarsu.web.profile.user.utils.UserField
import ru.yarsu.web.profile.user.utils.toPathString

@Suppress("detekt:LongParameterList")
class UserRoomVM(
    val user: User,
    val form: CustomWebForm? = null,
    val fieldType: UserField? = null,
) : ViewModel {
    val changeVKLink = "$USER/${user.login}/${UserField.VK_LINK.toPathString()}"
    val changeEmailLink = "$USER/${user.login}/${UserField.EMAIL.toPathString()}"
    val changePhoneLink = "$USER/${user.login}/${UserField.PHONE_NUMBER.toPathString()}"
    val changePasswordLink = "$USER/${user.login}/${UserField.PASSWORD.toPathString()}"
    val changeNameLink = "$USER/${user.login}/${UserField.NAME.toPathString()}"
    val changeSurnameLink = "$USER/${user.login}/${UserField.SURNAME.toPathString()}"

    val showPasswordModal: Boolean = fieldType?.takeIf { it == UserField.PASSWORD } != null
    val showNameModal: Boolean = fieldType?.takeIf { it == UserField.NAME } != null
    val showSurnameModal: Boolean = fieldType?.takeIf { it == UserField.SURNAME } != null
    val showEmailModal: Boolean = fieldType?.takeIf { it == UserField.EMAIL } != null
    val showPhoneModal: Boolean = fieldType?.takeIf { it == UserField.PHONE_NUMBER } != null
    val showVKModal: Boolean = fieldType?.takeIf { it == UserField.VK_LINK } != null
}
