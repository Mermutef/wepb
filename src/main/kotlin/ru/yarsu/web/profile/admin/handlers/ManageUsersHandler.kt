@file:Suppress("KotlinUnreachableCode")

package ru.yarsu.web.profile.admin.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.failureOrNull
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.core.body.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.domain.operations.users.MakeRoleError
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.internalServerError
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.admin.models.ManageUsersVM

class ManageUsersHandler (
    val renderer: ContextAwareViewRender,
    private val operations: OperationsHolder,
    val userLens: RequestContextLens<User?>,
) : HttpHandler {

    override fun invoke(request: Request): Response =
        userLens(request)?.let { user ->
            val error = if (request.form().isNotEmpty()) {
                RoleFormManager(request.form(), operations).trySetRoleAndGetError()
            } else {
                null
            }
            val users = operations.userOperations.fetchAllUsers().onFailure {
                return internalServerError
            }.sortedBy { it.id }
            val model = ManageUsersVM(users, manageUsersRoles(), error)
            return renderer(request) extract model
        } ?: notFound

    class RoleFormManager(val form: Form, private val operations: OperationsHolder) {

        fun trySetRoleAndGetError(): Pair<Int?, String?> =
            when (val setRoleResult = trySetUserRole()) {
                is Success -> Pair(setRoleResult.value.id, null)
                is Failure -> Pair(setRoleResult.reason.first, setRoleResult.reason.second.errorText)
            }

        @Suppress("detekt:CyclomaticComplexMethod")
        private fun trySetUserRole(): Result<User, Pair<Int?, ChangeRoleError>> =
            when (val validationResult = validateUserForm()) {
                is Success -> {
                    val user = validationResult.value.first
                    val newRole = validationResult.value.second
                    when (newRole) {
                        Role.READER -> {
                            when (operations.userOperations.makeReader(user).failureOrNull()) {
                                MakeRoleError.IS_ALREADY_READER ->
                                    Success(user)
                                null -> Success(user)
                                else ->
                                    Failure(Pair(user.id, ChangeRoleError.UNKNOWN_DATABASE_ERROR))
                            }
                        }

                        Role.WRITER -> {
                            when (operations.userOperations.makeWriter(user).failureOrNull()) {
                                MakeRoleError.IS_ALREADY_WRITER ->
                                    Success(user)
                                null -> Success(user)
                                else ->
                                    Failure(Pair(user.id, ChangeRoleError.UNKNOWN_DATABASE_ERROR))
                            }
                        }

                        Role.MODERATOR -> {
                            when (operations.userOperations.makeModerator(user).failureOrNull()) {
                                MakeRoleError.IS_ALREADY_MODERATOR ->
                                    Success(user)
                                null -> Success(user)
                                else ->
                                    Failure(Pair(user.id, ChangeRoleError.UNKNOWN_DATABASE_ERROR))
                            }
                        }

                        Role.ADMIN -> {
                            if (user.role == Role.ADMIN) {
                                Success(user)
                            } else {
                                Failure(Pair(user.id, ChangeRoleError.INVALID_NEW_ROLE))
                            }
                        }

                        else -> Failure(Pair(user.id, ChangeRoleError.INVALID_NEW_ROLE))
                    }
                }

                is Failure -> Failure(Pair(validationResult.reason.first, validationResult.reason.second))
            }

        private fun validateUserForm(): Result<Pair<User, Role>, Pair<Int?, ChangeRoleError>> {
            val id = form.findSingle("id")?.toIntOrNull()
            val role = form.findSingle("role")
            val newRole = Role.entries.find { it.toString() == role }
            val user = id?.let { operations.userOperations.fetchUserByID(it).valueOrNull() }

            return if (id == null || role == null || newRole == null) {
                Failure(Pair(id, ChangeRoleError.INVALID_FORM_DATA))
            } else if (user == null) {
                Failure(Pair(id, ChangeRoleError.INVALID_FORM_DATA))
            } else {
                val roleSet = Role.entries.minus(Role.ADMIN)
                when {
                    user.role in roleSet && newRole in roleSet ->
                        Success(Pair(user, newRole))

                    user.role == Role.ADMIN && newRole == Role.ADMIN ->
                        Success(Pair(user, newRole))

                    user.role in roleSet ->
                        Failure(Pair(id, ChangeRoleError.INVALID_NEW_ROLE))

                    else ->
                        Failure(Pair(id, ChangeRoleError.ROLE_CHANGE_NOT_ALLOWED))
                }
            }
        }
    }
}

private fun manageUsersRoles(): List<Role> = Role.entries.minus(Role.ADMIN)

enum class ChangeRoleError(val errorText: String) {
    INVALID_FORM_DATA("Неверные данные"),
    ROLE_CHANGE_NOT_ALLOWED("Данный пользователь не поддерживает смену роли"),
    INVALID_NEW_ROLE("Невозможно сменить роль пользователя на выбранную"),
    UNKNOWN_DATABASE_ERROR("Что-то случилось. Повторите попытку позднее"),
}
