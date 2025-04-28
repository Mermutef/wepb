package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.Post

interface PostToMediaOperations {
    fun selectPostToMediaByID(id: Int): Pair<Int, String>?

    fun selectPostToMediaByPostID(postId: Int): List<String>

    fun selectPostToMediaByMediaID(mediaFile: String): List<Int>

    fun selectAllPostToMedia(): List<Pair<Int, String>>

    fun insertPostToMedia(
        postId: Int,
        mediaId: String
    ): Pair<Int, String>?

    fun updateMediaFile(
        Id: Int,
        newMediaFile: String
    ): Pair<Int, String>?
}