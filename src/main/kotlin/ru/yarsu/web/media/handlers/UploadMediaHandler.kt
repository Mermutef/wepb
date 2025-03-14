package ru.yarsu.web.media.handlers

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.*
import org.http4k.lens.MultipartForm
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.media.MediaCreationError
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.web.lenses.GeneralWebLenses.from
import ru.yarsu.web.lenses.MediaLenses.fileField
import ru.yarsu.web.lenses.MediaLenses.mediaFileForm
import ru.yarsu.web.lenses.MediaLenses.mediaTypeField
import ru.yarsu.web.notFound
import ru.yarsu.web.ok
import java.time.LocalDateTime

class UploadMediaHandler(
    private val userLens: RequestContextLens<User?>,
    private val mediaOperations: MediaOperationsHolder,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val user = userLens(request) ?: return notFound
        val filesForm = mediaFileForm from request
        return when {
            filesForm.errors.isNotEmpty() -> ok("Error: Invalid request format")
            else -> filesForm.createMedia(user)
        }
    }

    private fun MultipartForm.createMedia(user: User): Response {
        val file = fileField from this
        val mediaType = mediaTypeField from this
        return when (
            val createdMedia = mediaOperations.createMedia(
                file.filename,
                user,
                mediaType,
                LocalDateTime.now(),
                file.content.readAllBytes(),
                true,
            )
        ) {
            is Success -> ok(createdMedia.value.filename)

            is Failure -> ok("Error: ${createdMedia.reason.uiMessage()}")
        }
    }

    private fun MediaCreationError.uiMessage() =
        when (this) {
            MediaCreationError.MEDIA_IS_EMPTY -> MediaUploadingError.MEDIA_IS_EMPTY.errorText
            MediaCreationError.MEDIA_ALREADY_EXIST -> MediaUploadingError.MEDIA_ALREADY_EXIST.errorText
            MediaCreationError.FILENAME_IS_TOO_LONG -> MediaUploadingError.FILENAME_IS_TOO_LONG.errorText
            MediaCreationError.FILENAME_PATTERN_MISMATCH -> MediaUploadingError.FILENAME_PATTERN_MISMATCH.errorText
            MediaCreationError.FILENAME_ALREADY_EXIST -> MediaUploadingError.FILENAME_ALREADY_EXIST.errorText
            MediaCreationError.FILENAME_IS_BLANK_OR_EMPTY -> MediaUploadingError.FILENAME_IS_BLANK_OR_EMPTY.errorText
            MediaCreationError.MEDIA_IS_TOO_LARGE -> MediaUploadingError.MEDIA_IS_TOO_LARGE.errorText
            MediaCreationError.UNKNOWN_DATABASE_ERROR -> MediaUploadingError.UNKNOWN_DATABASE_ERROR.errorText
        }
}

enum class MediaUploadingError(val errorText: String) {
    MEDIA_IS_EMPTY("Empty media file"),
    MEDIA_IS_TOO_LARGE("Media file is too large"),
    MEDIA_ALREADY_EXIST("Media already exist"),
    FILENAME_IS_TOO_LONG("Too long filename. It can have length less than ${MediaFile.MAX_FILENAME_LENGTH}"),
    FILENAME_PATTERN_MISMATCH(
        "The filename contains invalid characters. " +
            "It can contain only Latin letters and decimal digits and \".\", \"-\", \"_\""
    ),
    FILENAME_ALREADY_EXIST("Another media with this filename already exist"),
    FILENAME_IS_BLANK_OR_EMPTY("Media filename is blank or empty"),
    UNKNOWN_DATABASE_ERROR("Something went wrong, try again later"),
}
