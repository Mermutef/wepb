package ru.yarsu.domain.dependencies

import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.Post
import java.time.LocalDateTime

interface HashtagOperations {
    fun selectHashtagByID(hashtagId: Int): Hashtag?

    fun selectHashtagByTitle(title: String): Hashtag?

    fun selectAllHashtags(): List<Post>

    fun insertHashtag(
        title: String
    ): Hashtag?

    fun updateTitle(
        hashtagId: Int,
        newTitle: String
    ): Hashtag?
}