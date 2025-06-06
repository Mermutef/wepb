package ru.yarsu.web.posts.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.web.rendering.MarkdownToHTMLRenderer

class PostVM(
    val postWithHashtag: Pair<Post, Hashtag>,
    val author: User,
) : ViewModel {
    fun renderMD(content: String) = MarkdownToHTMLRenderer.render(content)
}
