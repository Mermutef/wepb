package ru.yarsu.web.posts.handlers

import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.OperationsHolder
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.form.toCustomForm
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.posts.lenses.PostWebLenses.contentField
import ru.yarsu.web.posts.lenses.PostWebLenses.eventDateField
import ru.yarsu.web.posts.lenses.PostWebLenses.hashtagField
import ru.yarsu.web.posts.lenses.PostWebLenses.previewField
import ru.yarsu.web.posts.lenses.PostWebLenses.titleField
import ru.yarsu.web.posts.models.NewPostVM
import ru.yarsu.web.posts.utils.canEdit

@Suppress("detekt:ReturnCount")
class ShowEditPostFormHandler(
    private val render: ContextAwareViewRender,
    private val operations: OperationsHolder,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val postId = request.idOrNull() ?: return notFound
        val user = userLens(request) ?: return notFound
        val post = operations.postOperations.fetchPostById(postId).valueOrNull() ?: return notFound
        return when {
            user canEdit post -> request.responseWithForm(post)

            else -> notFound
        }
    }

    private fun Request.responseWithForm(post: Post) =
        render(this) extract NewPostVM(
            form = WebForm().with(
                titleField of post.title,
                previewField of post.preview,
                contentField of post.content,
                hashtagField of post.hashtagId,
                eventDateField of post.eventDate,
            ).toCustomForm(),
            hashTags = operations.hashtagOperations
                .fetchAllHashtags()
                .valueOrNull()
                ?: emptyList()
        )
}
