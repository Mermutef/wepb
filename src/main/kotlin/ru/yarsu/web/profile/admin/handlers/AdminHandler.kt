package ru.yarsu.web.profile.admin.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.notFound
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.extract
import ru.yarsu.web.profile.admin.models.AdminRoomVM

class AdminHandler(
    private val render: ContextAwareViewRender,
    //private val userOperations : UserOperationsHolder,
    private val userOperations: UserOperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {

    override fun invoke(request: Request): Response {
        return when (val user = request.authorizeUserFromPath(userLens = userLens)) {
            is Failure -> notFound
            is Success -> {
                if (user.value.role != Role.ADMIN) {
                    notFound
                } else {
                    when (val usersList = fetchAllUsers()) {
                        is Failure -> notFound
                        is Success -> render(request) extract AdminRoomVM(usersList.value)
                    }
                }
            }
        }
    }
    private fun fetchAllUsers(): Result<List<User>, FetchingUsersError> {
        val testUsers = listOf(
            User(
                id = 1,
                name = "Test",
                surname = "User",
                login = "user",
                email = "test@test.com",
                phoneNumber = "79621851367",
                password = "aaa",
                role = Role.READER,
                vkLink = TODO()
            )
        )
        return Success(testUsers)
    }


//    private fun fetchAllUsers(): Result<List<User>, FetchingUsersError> {
//        return when (val users = userOperations.fetchAllUsers()) {
//            is Failure -> when (users.reason) {
//                UserFetchingError.NO_SUCH_USER -> Failure(FetchingUsersError.NO_USERS_FOUND)
//                UserFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingUsersError.UNKNOWN_DATABASE_ERROR)
//            }
//
//            is Success -> Success(users.value)
//        }
//    }
}

enum class FetchingUsersError(val errorText: String) {
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    NO_USERS_FOUND("Пользователи не найдены"),
}
