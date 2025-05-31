package ru.yarsu.web.common.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MIDDLE_HASHTAG_LENGTH
import ru.yarsu.domain.models.Post
import ru.yarsu.web.profile.crop
import ru.yarsu.web.rendering.MarkdownToHTMLRenderer

class GeneralPageVM(
    val postsWithHashtag: Map<Post, Hashtag>,
) : ViewModel {
    fun postLink(post: Post) = "posts/${post.id}"

    fun middleHashtag(hashtag: Hashtag) = hashtag.title.crop(MIDDLE_HASHTAG_LENGTH)

    fun renderMD(content: String) = MarkdownToHTMLRenderer.render(content)
}
