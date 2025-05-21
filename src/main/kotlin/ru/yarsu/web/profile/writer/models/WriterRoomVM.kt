package ru.yarsu.web.profile.writer.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MIDDLE_HASHTAG_LENGTH
import ru.yarsu.domain.models.MIDDLE_TITLE_LENGTH
import ru.yarsu.domain.models.Post
import ru.yarsu.web.profile.crop

class WriterRoomVM(
    val postsWithHashtag: Map<Post, Hashtag>,
) : ViewModel {
    fun postLink(post: Post) = "posts/${post.id}"

    fun middleHashtag(hashtag: Hashtag) = hashtag.title.crop(MIDDLE_HASHTAG_LENGTH)
}
