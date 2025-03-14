package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User

class FetchMediaWithMeta(
    val fetchMediaWithMeta: (String) -> MediaFile?,
) : (String) -> Result<MediaFile, MediaFetchingError> {
    override fun invoke(filename: String): Result<MediaFile, MediaFetchingError> =
        try {
            when (val fetchedMedia = fetchMediaWithMeta(filename)) {
                is MediaFile -> Success(fetchedMedia)
                else -> Failure(MediaFetchingError.NO_SUCH_MEDIA)
            }
        } catch (_: DataAccessException) {
            Failure(MediaFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchOnlyMedia(
    val fetchOnlyMedia: (String) -> ByteArray?,
) : (String) -> Result<ByteArray, MediaFetchingError> {
    override fun invoke(filename: String): Result<ByteArray, MediaFetchingError> =
        try {
            when (val fetchedMedia = fetchOnlyMedia(filename)) {
                is ByteArray -> Success(fetchedMedia)
                else -> Failure(MediaFetchingError.NO_SUCH_MEDIA)
            }
        } catch (_: DataAccessException) {
            Failure(MediaFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchOnlyMeta(
    val fetchOnlyMeta: (String) -> MediaFile?,
) : (String) -> Result<MediaFile, MediaFetchingError> {
    override fun invoke(filename: String): Result<MediaFile, MediaFetchingError> =
        try {
            when (val fetchedMedia = fetchOnlyMeta(filename)) {
                is MediaFile -> Success(fetchedMedia)
                else -> Failure(MediaFetchingError.NO_SUCH_MEDIA)
            }
        } catch (_: DataAccessException) {
            Failure(MediaFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchMediaByUserAndMediaType(
    val fetchMediaByUserIDAndMediaType: (userID: Int, mediaType: MediaType) -> List<MediaFile>,
) : (User, MediaType) -> Result<List<MediaFile>, MediaFetchingError> {
    override fun invoke(
        user: User,
        mediaType: MediaType,
    ): Result<List<MediaFile>, MediaFetchingError> =
        try {
            Success(fetchMediaByUserIDAndMediaType(user.id, mediaType))
        } catch (_: DataAccessException) {
            Failure(MediaFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class MediaFetchingError {
    NO_SUCH_MEDIA,
    UNKNOWN_DATABASE_ERROR,
}
