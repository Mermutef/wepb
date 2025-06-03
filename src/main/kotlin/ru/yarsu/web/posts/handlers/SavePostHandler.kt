package ru.yarsu.web.posts.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.domain.operations.posts.PostOperationsHolder
import ru.yarsu.web.context.templates.ContextAwareViewRender
import ru.yarsu.web.extract
import ru.yarsu.web.form.toCustomForm
import ru.yarsu.web.lenses.GeneralWebLenses.idOrNull
import ru.yarsu.web.lenses.GeneralWebLenses.lensOrNull
import ru.yarsu.web.notFound
import ru.yarsu.web.posts.lenses.PostWebLenses
import ru.yarsu.web.posts.models.NewPostVM
import ru.yarsu.web.posts.utils.responseWithError
import ru.yarsu.web.posts.utils.tryAddPostAndHashtag
import ru.yarsu.web.posts.utils.validateForm

class SavePostHandler(
    private val postOperations: PostOperationsHolder,
    private val hashtagOperations: HashtagOperationsHolder,
    private val mediaOperations: MediaOperationsHolder,
    private val userLens: RequestContextLens<User?>,
    private val render: ContextAwareViewRender,
) : HttpHandler {
    @Suppress("ReturnCount")
    override fun invoke(request: Request): Response {
        return lensOrNull(userLens, request)?.let { user ->
            val postId = request.idOrNull()
            val form = PostWebLenses.formFieldAll(request)
            println(form)
            if (form.errors.isNotEmpty()) {
                return render(request) extract
                    NewPostVM(
                        form = form.toCustomForm(),
                        hashTags = hashtagOperations
                            .fetchAllHashtags()
                            .valueOrNull()
                            ?: emptyList()
                    )
            }
            // todo редактирование постов
            return when (
                val validatedPostAndHashtag = form.validateForm(
                    user = user,
                    hashtagOperations = hashtagOperations,
                    postId = postId,
                )
            ) {
                is Failure -> request.responseWithError(
                    name = validatedPostAndHashtag.reason.first,
                    description = validatedPostAndHashtag.reason.second,
                    render = render,
                    hashtagOperations = hashtagOperations,
                    form = form,
                )

                is Success -> {
                    // validatedForm.value.takeIf { it.id == -1 }
                    // ?.let {
                    request.tryAddPostAndHashtag(
                        postAndHashtag = validatedPostAndHashtag.value,
                        postOperations = postOperations,
                        mediaOperations = mediaOperations,
                        hashtagOperations = hashtagOperations,
                        render = render,
                        form = form
                    )
                }
                // }
                // ?: tryUpdatePost()
            }
        } ?: notFound
    }
}
