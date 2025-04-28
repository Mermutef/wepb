package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post

interface PostToHashtagOperations {
    fun selectPostToHashtagByID(id: Int): Pair<Int, Hashtag>?

    fun selectPostToHashtagByPostID(postId: Int): List<Hashtag>

    fun selectPostToHashtagByHashtagID(hashtagId: Int): List<Int>

    fun selectAllPostsToHashtags(): List<Pair<Int, Hashtag>>

    fun insertPostToHashtag(
        postId: Int,
        hashtagId: Int
    ): Pair<Int, Hashtag>?

    fun updateHashtagId(
        id: Int,
        newHashtagId: Int
    ): Pair<Int, Hashtag>
}