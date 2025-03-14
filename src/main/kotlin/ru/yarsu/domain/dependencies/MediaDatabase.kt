package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import java.time.LocalDateTime

interface MediaDatabase {
    @Suppress("detekt:LongParameterList")
    fun insertMedia(
        filename: String,
        authorId: Int,
        mediaType: MediaType,
        birthDate: LocalDateTime = LocalDateTime.now(),
        content: ByteArray,
        isTemporary: Boolean = true,
    ): MediaFile?

    fun makeTemporary(filename: String): MediaFile?

    fun makeRegular(filename: String): MediaFile?

    fun deleteMedia(filename: String)

    fun selectMediaWithMeta(filename: String): MediaFile?

    fun selectOnlyMedia(filename: String): ByteArray?

    /**
     * Field `content` of returns value always empty byte array
     */
    fun selectOnlyMeta(filename: String): MediaFile?

    fun selectMediaByUserIDAndMediaType(
        userID: Int,
        mediaType: MediaType,
    ): List<MediaFile>
}
