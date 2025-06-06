package ru.yarsu.domain.operations.users

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.mockito.Mockito.mock
import ru.yarsu.db.appConfig
import ru.yarsu.db.users.UserOperations

class UserOperationsHolderTest : FunSpec({
    val context: DSLContext = mock()

    val usersOperations = UserOperations(context)
    val userOperationsHolder = UserOperationsHolder(usersOperations, appConfig)

    test("UserOperationsHolder should initialize with provided user operations") {
        userOperationsHolder.createUser::class.shouldBe(CreateUser::class)
        userOperationsHolder.fetchAllUsers::class.shouldBe(FetchAllUsers::class)
        userOperationsHolder.fetchUserByID::class.shouldBe(FetchUserByID::class)
        userOperationsHolder.fetchUserByEmail::class.shouldBe(FetchUserByEmail::class)
        userOperationsHolder.fetchUserByPhone::class.shouldBe(FetchUserByPhone::class)
        userOperationsHolder.fetchUserByLogin::class.shouldBe(FetchUserByLogin::class)
        userOperationsHolder.changeName::class.shouldBe(ChangeStringFieldInUser::class)
        userOperationsHolder.changeSurname::class.shouldBe(ChangeStringFieldInUser::class)
        userOperationsHolder.changeEmail::class.shouldBe(ChangeStringFieldInUser::class)
        userOperationsHolder.changePhoneNumber::class.shouldBe(ChangeStringFieldInUser::class)
        userOperationsHolder.changeVKLink::class.shouldBe(ChangeStringFieldInUser::class)
        userOperationsHolder.makeReader::class.shouldBe(RoleChanger::class)
        userOperationsHolder.makeWriter::class.shouldBe(RoleChanger::class)
        userOperationsHolder.makeModerator::class.shouldBe(RoleChanger::class)
        userOperationsHolder.changePassword::class.shouldBe(ChangePassword::class)
        userOperationsHolder.fetchUsersByRole::class.shouldBe(FetchUsersByRole::class)
    }
})
