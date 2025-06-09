package ru.yarsu.web.profile.admin.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.core.body.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.db.users.UserOperations
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
    private val userOperations: UserOperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {

    override fun invoke(request: Request): Response {
        return when{
            request.method == Method.GET-> showUserList(request)

            request.method == Method.POST-> processRoleChange(request)

            else-> Response(Status.METHOD_NOT_ALLOWED)
        }

    }

    private fun showUserList(request: Request,error: ChangeRoleError? = null,changedUserId: Int? = null):Response{
        return when(val user = request.authorizeUserFromPath(userLens = userLens)){
            is Failure -> notFound
            is Success -> {
                if (user.value.role != Role.ADMIN){
                    notFound
                }else{
                    when(val userList = fetchAllUsers()){
                        is Failure -> notFound
                        is Success ->  render(request) extract  AdminRoomVM(userList.value, error)
                    }
                }
            }
        }
    }
    private fun List<Pair<String, String?>>.getFormField(key: String): String? {
        return this.firstOrNull { it.first == key }?.second
    }
    private fun processRoleChange(request: Request):Response{
        val form = request.form()

        val userId = form.getFormField("userId")?.toIntOrNull()
        val newRoleStr = form.getFormField("newRole")

        if (userId == null || newRoleStr == null){
            return showUserList(request, ChangeRoleError.INVALID_FORM_DATA)
        }

        val newRole = try{
            Role.valueOf(newRoleStr)
        }catch (e: IllegalArgumentException){
            return showUserList(request, ChangeRoleError.INVALID_ROLE)
        }
         return when (val result = changeUserRole(userId,newRole)){
             is Success ->showUserList(request)
             is Failure -> showUserList(request,ChangeRoleError.INVALID_NEW_ROLE)
         }
    }

        private fun changeUserRole(userId: Int, newRole: Role): Result<User?, Pair<Int?, ChangeRoleError>> {
        val user = userOperations.fetchUserByID(userId).valueOrNull()
            ?: return Failure(Pair(userId, ChangeRoleError.USER_NOT_FOUND))

        return when {
            user.role == Role.ADMIN ->
                Failure(Pair(userId, ChangeRoleError.CANNOT_CHANGE_ADMIN_ROLE))

            newRole == Role.WRITER -> {
                 when (val result = userOperations.makeWriter(user)) {
                    is Success -> Success(result.value)
                    is Failure -> Failure(Pair(userId, ChangeRoleError.DATABASE_ERROR))
                }
            }
            newRole == Role.MODERATOR -> {
                when (val result = userOperations.makeModerator(user)) {
                    is Success -> Success(result.value)
                    is Failure -> Failure(Pair(userId, ChangeRoleError.DATABASE_ERROR))
                }
            }
            newRole == Role.READER -> {
                when ( val result =userOperations.makeReader(user)){
                    is Success -> Success(result.value)
                    is Failure -> Failure(Pair(userId, ChangeRoleError.DATABASE_ERROR))
                }
            }
            else ->  Failure(Pair(userId, ChangeRoleError.DATABASE_ERROR))

        }
    }

    private fun fetchAllUsers(): Result<List<User>, FetchingUsersError> {
        return when (val users = userOperations.fetchAllUsers()) {
            is Failure -> when (users.reason) {
                UserFetchingError.NO_SUCH_USER -> Failure(FetchingUsersError.NO_USERS_FOUND)
                UserFetchingError.UNKNOWN_DATABASE_ERROR -> Failure(FetchingUsersError.UNKNOWN_DATABASE_ERROR)
            }

            is Success -> Success(users.value)
        }
    }
}

enum class ChangeRoleError(val errorMessage: String) {
    INVALID_NEW_ROLE("Не удалось изменить роль"),
    INVALID_FORM_DATA("Неверные данные формы"),
    INVALID_ROLE("Указана недопустимая роль"),
    USER_NOT_FOUND("Пользователь не найден"),
    CANNOT_CHANGE_ADMIN_ROLE("Нельзя изменить роль администратора"),
    DATABASE_ERROR("Ошибка базы данных при изменении роли"),


}


enum class FetchingUsersError(val errorText: String) {
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Пожалуйста, повторите попытку позднее или обратитесь за помощью"),
    NO_USERS_FOUND("Пользователи не найдены"),
}
