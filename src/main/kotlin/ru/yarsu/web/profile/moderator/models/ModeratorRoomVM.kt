package ru.yarsu.web.profile.moderator.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MIDDLE_HASHTAG_LENGTH
import ru.yarsu.domain.models.MIDDLE_TITLE_LENGTH
import ru.yarsu.domain.models.Post
import ru.yarsu.web.profile.crop

class ModeratorRoomVM(
    val postsWithHashtag: Map<Post, Hashtag>,
) : ViewModel {
    //todo добавить ссылку на пост
    fun postLink(post: Post) = "posts/${post.id}"

    fun middleHashtag(hashtag: Hashtag) = hashtag.title.crop(MIDDLE_HASHTAG_LENGTH)
}
