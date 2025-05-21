package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.Hashtag

interface HashtagsDatabase {
    fun selectHashtagByID(hashtagId: Int): Hashtag?

    fun selectHashtagByTitle(title: String): Hashtag?

    fun selectAllHashtags(): List<Hashtag>

    fun insertHashtag(title: String): Hashtag?

    fun updateTitle(
        hashtagId: Int,
        newTitle: String,
    ): Hashtag?

    fun deleteHashtagById(hashtagId: Int): Int?
}
