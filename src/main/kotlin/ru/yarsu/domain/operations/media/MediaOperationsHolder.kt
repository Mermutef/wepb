package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.Result
import ru.yarsu.domain.dependencies.MediaDatabase
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class MediaOperationsHolder(private val mediaDatabase: MediaDatabase) {
    val createMedia: (
        filename: String,
        author: User,
        mediaType: MediaType,
        birthDate: LocalDateTime,
        content: ByteArray,
        isTemporary: Boolean,
    ) -> Result<MediaFile, MediaCreationError> = CreateMedia(mediaDatabase::selectOnlyMedia, mediaDatabase::insertMedia)

    val makeMediaTemporary: (media: MediaFile) -> Result<MediaFile, MakeMediaTemporaryOrRegularError> =
        MakeMediaTemporary(mediaDatabase::makeTemporary)

    val makeMediaRegular: (media: MediaFile) -> Result<MediaFile, MakeMediaTemporaryOrRegularError> =
        MakeMediaRegular(mediaDatabase::makeRegular)

    val removeMedia: (media: MediaFile) -> Result<Boolean, MediaRemovingError> = RemoveMedia(mediaDatabase::deleteMedia)

    val fetchMediaWithMeta: (filename: String) -> Result<MediaFile, MediaFetchingError> =
        FetchMediaWithMeta(mediaDatabase::selectMediaWithMeta)

    val fetchOnlyMedia: (filename: String) -> Result<ByteArray, MediaFetchingError> =
        FetchOnlyMedia(mediaDatabase::selectOnlyMedia)

    /**
     * Field `content` of returns value always empty byte array
     */
    val fetchOnlyMeta: (filename: String) -> Result<MediaFile, MediaFetchingError> =
        FetchOnlyMeta(mediaDatabase::selectOnlyMeta)

    val fetchMediaByUserAndMediaType: (
        user: User,
        mediaType: MediaType,
    ) -> Result<List<MediaFile>, MediaFetchingError> =
        FetchMediaByUserAndMediaType(mediaDatabase::selectMediaByUserIDAndMediaType)
}
