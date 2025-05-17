package ru.yarsu.web.profile.moderator.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MIDDLE_HASHTAG_LENGTH
import ru.yarsu.domain.models.MIDDLE_TITLE_LENGTH
import ru.yarsu.domain.models.Post

class ModeratorRoomVM(
    val postsWithHashtag: Map<Post, Hashtag>,
) : ViewModel {
    fun postLink(post: Post) = "posts/${post.id}"

    fun middleHashtag(hashtag: Hashtag) =
        when {
            hashtag.title.length < MIDDLE_TITLE_LENGTH -> hashtag.title
            else -> "${hashtag.title.slice(0..MIDDLE_HASHTAG_LENGTH)}..."
        }
}
