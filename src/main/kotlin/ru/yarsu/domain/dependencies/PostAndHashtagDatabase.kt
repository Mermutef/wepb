package ru.yarsu.domain.dependencies

interface PostAndHashtagDatabase {
    fun selectPostAndHashtagByID(id: Int): Pair<Int, Int>?

    fun selectPostAndHashtagByPostID(postId: Int): List<Int>

    fun selectPostAndHashtagByHashtagID(hashtagId: Int): List<Int>

    fun selectAllPostsAndHashtags(): List<Pair<Int, Int>>

    fun insertPostAndHashtag(
        postId: Int,
        hashtagId: Int
    ): Pair<Int, Int>?

    fun updateHashtagId(
        id: Int,
        newHashtagId: Int
    ): Pair<Int, Int>
}