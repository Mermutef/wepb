package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.MediaValidationResult
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class CreateMedia(
    val fetchOnlyMedia: (String) -> ByteArray?,
    val createMedia: (
        filename: String,
        authorID: Int,
        mediaType: MediaType,
        birthDate: LocalDateTime,
        content: ByteArray,
        isTemporary: Boolean,
    ) -> MediaFile?,
) : (
        String,
        User,
        MediaType,
        LocalDateTime,
        ByteArray,
        Boolean,
    ) -> Result<MediaFile, MediaCreationError> {
    override fun invoke(
        filename: String,
        author: User,
        mediaType: MediaType,
        birthDate: LocalDateTime,
        content: ByteArray,
        isTemporary: Boolean,
    ): Result<MediaFile, MediaCreationError> {
        return try {
            thisMediaInvalidOrExists(fetchOnlyMedia, filename, content)?.let { return it }
            when (
                val createdMedia = createMedia(
                    filename,
                    author.id,
                    mediaType,
                    birthDate,
                    content,
                    isTemporary,
                )
            ) {
                is MediaFile -> Success(createdMedia)
                else -> Failure(MediaCreationError.UNKNOWN_DATABASE_ERROR)
            }
        } catch (_: DataAccessException) {
            Failure(MediaCreationError.UNKNOWN_DATABASE_ERROR)
        }
    }

    private fun thisMediaInvalidOrExists(
        fetchOnlyMedia: (String) -> ByteArray?,
        filename: String,
        content: ByteArray,
    ): Failure<MediaCreationError>? {
        val mediaValidationResult = MediaFile.validateMediaData(
            filename,
            content
        )
        if (mediaValidationResult != MediaValidationResult.ALL_OK) {
            return Failure(mediaValidationResult.toMediaCreationError())
        }
        return when (val mediaInDB = fetchOnlyMedia(filename)) {
            is ByteArray -> when {
                content.isEqualsTo(mediaInDB) -> Failure(MediaCreationError.MEDIA_ALREADY_EXIST)
                else -> Failure(MediaCreationError.FILENAME_ALREADY_EXIST)
            }

            else -> null
        }
    }

    private fun MediaValidationResult.toMediaCreationError(): MediaCreationError =
        when (this) {
            MediaValidationResult.FILENAME_IS_TOO_LONG -> MediaCreationError.FILENAME_IS_TOO_LONG
            MediaValidationResult.FILENAME_IS_BLANK_OR_EMPTY -> MediaCreationError.FILENAME_IS_BLANK_OR_EMPTY
            MediaValidationResult.FILENAME_PATTERN_MISMATCH -> MediaCreationError.FILENAME_PATTERN_MISMATCH
            MediaValidationResult.CONTENT_IS_EMPTY -> MediaCreationError.MEDIA_IS_EMPTY
            MediaValidationResult.CONTENT_IS_TOO_LARGE -> MediaCreationError.MEDIA_IS_TOO_LARGE
            else -> MediaCreationError.UNKNOWN_DATABASE_ERROR
        }

    private fun ByteArray.isEqualsTo(other: ByteArray): Boolean =
        this.contentHashCode() == other.contentHashCode() && this.contentEquals(other)
}

enum class MediaCreationError {
    FILENAME_IS_BLANK_OR_EMPTY,
    FILENAME_IS_TOO_LONG,
    FILENAME_PATTERN_MISMATCH,
    MEDIA_IS_EMPTY,
    MEDIA_IS_TOO_LARGE,
    MEDIA_ALREADY_EXIST,
    FILENAME_ALREADY_EXIST,
    UNKNOWN_DATABASE_ERROR,
}
