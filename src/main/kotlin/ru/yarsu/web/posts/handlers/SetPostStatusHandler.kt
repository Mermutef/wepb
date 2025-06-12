package ru.yarsu.web.posts.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.posts.PostOperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.posts.POST_SEGMENT
import ru.yarsu.web.posts.lenses.StatusWebLenses
import ru.yarsu.web.posts.lenses.StatusWebLenses.statusField
import ru.yarsu.web.posts.utils.fetchPostById
import ru.yarsu.web.posts.utils.updatePostModeratorId
import ru.yarsu.web.posts.utils.updatePostStatus
import ru.yarsu.web.redirect

@Suppress("detekt:TooManyFunctions")
class SetPostStatusHandler(
    private val postOperations: PostOperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return lensOrNull(userLens, request)?.let { user ->
            val postId = request.idOrNull()
            return when {
                postId == null -> notFound
                else -> {
                    val form = StatusWebLenses.formStatusField(request)
                    if (form.errors.isNotEmpty()) notFound
                    val status = statusField(form)
                    fetchPostById(postId, postOperations)
                        .valueOrNull()
                        ?.let { tryChangeStatus(it, status, user) }
                        ?: notFound
                }
            }
        } ?: notFound
    }

    private fun tryChangeStatus(
        post: Post,
        status: Status,
        user: User,
    ) = when (status) {
        Status.DRAFT -> tryChangeToDraft(
            user = user,
            post = post,
            status = status
        )
        Status.HIDDEN -> tryChangeToHidden(
            user = user,
            post = post,
            status = status
        )
        Status.PUBLISHED -> tryChangeToPublish(
            user = user,
            post = post,
            status = status
        )
        Status.MODERATION -> tryChangeToModeration(
            user = user,
            post = post,
            status = status
        )
    }

    private fun tryChangeToDraft(
        user: User,
        post: Post,
        status: Status,
    ): Response {
        return if (user.canMakeDraft(post)) {
            tryChangeAndResponse(
                status = status,
                post = post,
                user = user
            )
        } else {
            notFound
        }
    }

    private fun tryChangeToModeration(
        user: User,
        post: Post,
        status: Status,
    ): Response {
        return if (user.canMakeModeration(post)) {
            tryChangeAndResponse(
                status = status,
                post = post,
                user = user
            )
        } else {
            notFound
        }
    }

    private fun tryChangeToHidden(
        user: User,
        post: Post,
        status: Status,
    ): Response {
        return if (user.canMakeHidden(post)) {
            tryChangeAndResponse(
                status = status,
                post = post,
                user = user
            )
        } else {
            notFound
        }
    }

    private fun tryChangeToPublish(
        user: User,
        post: Post,
        status: Status,
    ): Response {
        return if (user.canMakePublish(post)) {
            tryChangeAndResponse(
                status = status,
                post = post,
                user = user
            )
        } else {
            notFound
        }
    }

    private fun tryChangeAndResponse(
        status: Status,
        post: Post,
        user: User,
    ): Response {
        return when (
            updatePostStatus(
                status = status,
                post = post,
                postOperations = postOperations
            )
        ) {
            is Success -> {
                if (status == Status.PUBLISHED) updatePostModeratorId(post, user.id, postOperations)
                redirect("$POST_SEGMENT/${post.id}")
            }
            is Failure -> notFound
        }
    }

    private fun User.canMakeDraft(post: Post) =
        hasModerationPermissionsToDraft(post) ||
            hasWriterPermissionsToDraft(post)

    private fun User.canMakePublish(post: Post) = hasModerationPermissionsToPublish(post)

    private fun User.canMakeHidden(post: Post) = hasModerationPermissionsToHidden(post)

    private fun User.canMakeModeration(post: Post) =
        hasModerationPermissionsToModeration(post) ||
            hasWriterPermissionsToModeration(post)

    private fun User.hasModerationPermissionsToDraft(post: Post) =
        (this.role == Role.MODERATOR) &&
            (post.status == Status.MODERATION || post.status == Status.HIDDEN)

    private fun User.hasWriterPermissionsToDraft(post: Post) =
        (this.role == Role.WRITER && this.id == post.authorId) &&
            post.status == Status.MODERATION

    private fun User.hasModerationPermissionsToPublish(post: Post) =
        (this.role == Role.MODERATOR) &&
            (post.status == Status.DRAFT || post.status == Status.MODERATION)

    private fun User.hasModerationPermissionsToHidden(post: Post) =
        (this.role == Role.MODERATOR) &&
            post.status == Status.PUBLISHED

    private fun User.hasModerationPermissionsToModeration(post: Post) =
        (this.role == Role.MODERATOR) &&
            post.status == Status.DRAFT

    private fun User.hasWriterPermissionsToModeration(post: Post) =
        (this.role == Role.WRITER && this.id == post.authorId) &&
            post.status == Status.DRAFT
}
