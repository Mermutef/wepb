package ru.yarsu.web.profile.moderator.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MIDDLE_HASHTAG_LENGTH
import ru.yarsu.domain.models.Post
import ru.yarsu.web.posts.POST_SEGMENT
import ru.yarsu.web.profile.crop
import ru.yarsu.web.rendering.MarkdownToHTMLRenderer

class ModeratorRoomVM(
    val postsWithHashtag: Map<Post, Hashtag>,
) : ViewModel {
    fun postLink(post: Post) = "$POST_SEGMENT/${post.id}"

    fun renderMD(content: String) = MarkdownToHTMLRenderer.render(content)

    fun middleHashtag(hashtag: Hashtag) = hashtag.title.crop(MIDDLE_HASHTAG_LENGTH)
}
