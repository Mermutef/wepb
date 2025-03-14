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
        userOperationsHolder.fetchUserByName::class.shouldBe(FetchUserByName::class)
    }
})
