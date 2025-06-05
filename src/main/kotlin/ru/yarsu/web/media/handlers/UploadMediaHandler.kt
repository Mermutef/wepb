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
import ru.yarsu.web.media.lenses.MediaLenses.fileField
import ru.yarsu.web.media.lenses.MediaLenses.mediaFileForm
import ru.yarsu.web.media.lenses.MediaLenses.mediaTypeField
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
            filesForm.errors.isNotEmpty() -> ok("Error: Неверный формат запроса")
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
    MEDIA_IS_EMPTY("Пустой медиа файл"),
    MEDIA_IS_TOO_LARGE("Медиа файл слишком большой"),
    MEDIA_ALREADY_EXIST("Данный медиа файл уже существует"),
    FILENAME_IS_TOO_LONG("Слишком длинное имя файла. Оно должно быть менее ${MediaFile.MAX_FILENAME_LENGTH}"),
    FILENAME_PATTERN_MISMATCH(
        "Имя файла содержит недопустимые символы. " +
            "Допускаются латинские буквы, десятичные цифры, знаки \".\", \"-\", \"_\""
    ),
    FILENAME_ALREADY_EXIST("Медиа с данным именем файла уже существует"),
    FILENAME_IS_BLANK_OR_EMPTY("Пустое имя файла"),
    UNKNOWN_DATABASE_ERROR("Что-то пошло не так. Повторите попытку позднее"),
}
