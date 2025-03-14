package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.MediaFile

class MakeMediaTemporary(
    val makeMediaTemporary: (String) -> MediaFile?,
) : (MediaFile) -> Result<MediaFile, MakeMediaTemporaryOrRegularError> {
    override fun invoke(media: MediaFile): Result<MediaFile, MakeMediaTemporaryOrRegularError> {
        if (media.isTemporary) return Failure(MakeMediaTemporaryOrRegularError.NOTHING_TO_CHANGE)
        return try {
            when (val changingResult = makeMediaTemporary(media.filename)) {
                is MediaFile -> Success(changingResult)
                else -> Failure(MakeMediaTemporaryOrRegularError.UNKNOWN_CHANGING_ERROR)
            }
        } catch (_: DataAccessException) {
            Failure(MakeMediaTemporaryOrRegularError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

class MakeMediaRegular(
    val makeMediaRegular: (String) -> MediaFile?,
) : (MediaFile) -> Result<MediaFile, MakeMediaTemporaryOrRegularError> {
    override fun invoke(media: MediaFile): Result<MediaFile, MakeMediaTemporaryOrRegularError> {
        if (!media.isTemporary) return Failure(MakeMediaTemporaryOrRegularError.NOTHING_TO_CHANGE)
        return try {
            when (val changingResult = makeMediaRegular(media.filename)) {
                is MediaFile -> Success(changingResult)
                else -> Failure(MakeMediaTemporaryOrRegularError.UNKNOWN_CHANGING_ERROR)
            }
        } catch (_: DataAccessException) {
            Failure(MakeMediaTemporaryOrRegularError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

class RemoveMedia(
    val removeMedia: (String) -> Unit,
) : (MediaFile) -> Result<Boolean, MediaRemovingError> {
    override fun invoke(media: MediaFile): Result<Boolean, MediaRemovingError> {
        return try {
            removeMedia(media.filename)
            Success(true)
        } catch (_: DataAccessException) {
            Failure(MediaRemovingError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

enum class MediaRemovingError {
    UNKNOWN_DATABASE_ERROR,
}

enum class MakeMediaTemporaryOrRegularError {
    NOTHING_TO_CHANGE,
    UNKNOWN_CHANGING_ERROR,
    UNKNOWN_DATABASE_ERROR,
}
