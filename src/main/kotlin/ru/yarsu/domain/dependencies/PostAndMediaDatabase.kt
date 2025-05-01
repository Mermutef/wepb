package ru.yarsu.domain.dependencies

interface PostAndMediaDatabase {
    fun selectPostAndMediaByID(id: Int): Pair<Int, String>?

    fun selectPostAndMediaByPostID(postId: Int): List<String>

    fun selectPostAndMediaByMediaID(mediaFile: String): List<Int>

    fun selectAllPostsAndMedia(): List<Pair<Int, String>>

    fun insertPostAndMedia(
        postId: Int,
        mediaId: String
    ): Pair<Int, String>?

    fun updateMediaFile(
        Id: Int,
        newMediaFile: String
    ): Pair<Int, String>?
}