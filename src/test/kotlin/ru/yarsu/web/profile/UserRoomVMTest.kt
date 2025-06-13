package ru.yarsu.web.profile

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.web.form.CustomWebForm
import ru.yarsu.web.profile.user.USER
import ru.yarsu.web.profile.user.models.UserRoomVM
import ru.yarsu.web.profile.user.utils.UserField

class UserRoomVMTest : StringSpec({
    val testReader = User(
        id = 1,
        name = "Иван",
        surname = "Иванов",
        login = "writer",
        password = "password",
        email = "writer@test.com",
        phoneNumber = "79001234567",
        vkLink = "https://vk.com/writer",
        role = Role.READER
    )

    val testReader2 = User(
        id = 2,
        name = "Аван",
        surname = "Аванов",
        login = "ter",
        password = "password",
        email = "wr@test.com",
        phoneNumber = "79001234512",
        vkLink = "https://vk.com/wer",
        role = Role.READER
    )

    "Должны гененрироваться правильные ссылки" {
        val testReader = testReader2
        val vm = UserRoomVM(user = testReader)

        vm.changeVKLink shouldBe "$USER/ter/vk-link"
        vm.changeEmailLink shouldBe "$USER/ter/email"
        vm.changePhoneLink shouldBe "$USER/ter/phone-number"
        vm.changePasswordLink shouldBe "$USER/ter/password"
        vm.changeNameLink shouldBe "$USER/ter/name"
        vm.changeSurnameLink shouldBe "$USER/ter/surname"
    }

    "должны показывать модальные окна только для указанного типа поля" {
        val testUser = testReader

        val vmPassword = UserRoomVM(testUser, fieldType = UserField.PASSWORD)
        vmPassword.showPasswordModal shouldBe true
        vmPassword.showNameModal shouldBe false

        val vmEmail = UserRoomVM(testUser, fieldType = UserField.EMAIL)
        vmEmail.showEmailModal shouldBe true
        vmEmail.showVKModal shouldBe false

        val vmNone = UserRoomVM(testUser)
        vmNone.showPasswordModal shouldBe false
        vmNone.showEmailModal shouldBe false
    }

    "Должны генерироваться правильные ссылки вк" {
        val vm = UserRoomVM(testReader)

        vm.changeVKLink shouldBe "$USER/writer/vk-link"
        vm.changeNameLink shouldBe "$USER/writer/name"
    }

    "Должны быть видимы" {
        UserField.entries.forEach { field ->
            val vm = UserRoomVM(testReader, fieldType = field)

            when (field) {
                UserField.PASSWORD -> vm.showPasswordModal shouldBe true
                UserField.NAME -> vm.showNameModal shouldBe true
                UserField.SURNAME -> vm.showSurnameModal shouldBe true
                UserField.EMAIL -> vm.showEmailModal shouldBe true
                UserField.PHONE_NUMBER -> vm.showPhoneModal shouldBe true
                UserField.VK_LINK -> vm.showVKModal shouldBe true
            }
        }
    }

    "Формы должны обрабатываться" {
        val form = CustomWebForm()
        val vm = UserRoomVM(testReader, form = form)

        vm.form shouldBe form
    }

    "Обрабатывается наличие формы" {
        val testUser = testReader
        val testForm = CustomWebForm()

        val vmWithForm = UserRoomVM(testUser, form = testForm)
        vmWithForm.form shouldBe testForm

        val vmWithoutForm = UserRoomVM(testUser)
        vmWithoutForm.form shouldBe null
    }

    "Несколько пользователей обрабатывается" {
        val user1 = testReader
        val user2 = testReader2

        val vm1 = UserRoomVM(user1)
        vm1.changeVKLink shouldBe "$USER/writer/vk-link"

        val vm2 = UserRoomVM(user2)
        vm2.changeVKLink shouldBe "$USER/ter/vk-link"
    }

    "Обрабатывается пустой логин" {
        val user = testReader
        val vm = UserRoomVM(user)

        vm.changeVKLink shouldBe "$USER/writer/vk-link"
    }

    "Пустые form и fieldType обрабатываются" {
        val user = testReader
        val vm = UserRoomVM(user, null, null)

        vm.form shouldBe null
        vm.showPasswordModal shouldBe false
    }
})
