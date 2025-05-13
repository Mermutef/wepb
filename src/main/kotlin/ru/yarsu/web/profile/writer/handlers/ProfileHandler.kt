package ru.yarsu.web.profile.writer.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.users.UserOperationsHolder
import ru.yarsu.web.auth.lenses.UserWebLenses.authorizeUserFromPath
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.notFound
import ru.yarsu.web.profile.writer.models.WriterRoomVM

class ProfileHandler(
    private val render: ContextAwareViewRender,
    private val userOperations: UserOperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return when (
            val user = request.authorizeUserFromPath(
                userLens = userLens
            )
        ) {
            is Failure -> notFound
            is Success -> {
                val owner = user.value
                when {
                    owner.role == Role.WRITER -> WriterRoomVM()
                    owner.role == Role.MODERATOR -> WriterRoomVM(true)
                    // todo other rooms
                }
                render(request) extract WriterRoomVM()
            }
        }
    }
}
