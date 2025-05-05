package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.DirectionValidationResult
import ru.yarsu.domain.models.User
import ru.yarsu.domain.models.UserValidationResult
import ru.yarsu.domain.operations.users.UserCreationError
import java.time.LocalDateTime

class CreateDirection(
    val insertDirection: (
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairman_id: Int,
        deputy_сhairman_id: Int
    ) -> Direction?,
) : ( String, String, String, String, Int, Int ) -> Result<Direction, DirectionCreationError> {

    override fun invoke(
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairman_id: Int,
        deputy_сhairman_id: Int
    ): Result<Direction, DirectionCreationError> =
        when {
            Direction.validateDirectionData(name, description, logoPath, bannerPath, chairman_id, deputy_сhairman_id)
                    != DirectionValidationResult.ALL_OK ->
                        Failure(DirectionCreationError.INVALID_USER_DATA)
            nameAlreadyExists(name) ->
                Failure(DirectionCreationError.LOGIN_ALREADY_EXISTS)
            emailAlreadyExists(email) ->
                Failure(DirectionCreationError.EMAIL_ALREADY_EXISTS)
            phoneAlreadyExists(phoneNumber.filter { it.isDigit() }) ->
                Failure(DirectionCreationError.PHONE_ALREADY_EXISTS)
            else ->
                when (
                    val newUser = insertDirection(
                        name,
                        description,
                        logoPath,
                        bannerPath,
                        hasher.hash(pass),
                        vkLink,
                        role
                    )
                ) {
                    is User -> Success(newUser)
                    else -> Failure(DirectionCreationError.UNKNOWN_DATABASE_ERROR)
                }
    }

    private fun nameAlreadyExists(name: String): Boolean =
        when (fetchUserByName(name)) {
            is Direction -> true
            else -> false
        }

    private fun thisMediaInvalidOrExists(
        fetchOnlyMedia: (String) -> ByteArray?,
        filename: String,
        content: ByteArray,
    ): Failure<DirectionCreationError>? {
        val DirectionValidationResult = MediaFile.validateMediaData(
            filename,
            content
        )
        if (DirectionValidationResult != DirectionValidationResult.ALL_OK) {
            return Failure(DirectionValidationResult.toDirectionCreationError())
        }
        return when (val mediaInDB = fetchOnlyMedia(filename)) {
            is ByteArray -> when {
                content.isEqualsTo(mediaInDB) -> Failure(DirectionCreationError.MEDIA_ALREADY_EXIST)
                else -> Failure(DirectionCreationError.FILENAME_ALREADY_EXIST)
            }

            else -> null
        }
    }

    private fun DirectionValidationResult.toDirectionCreationError(): DirectionCreationError =
        when (this) {
            DirectionValidationResult.FILENAME_IS_TOO_LONG -> DirectionCreationError.FILENAME_IS_TOO_LONG
            DirectionValidationResult.FILENAME_IS_BLANK_OR_EMPTY -> DirectionCreationError.FILENAME_IS_BLANK_OR_EMPTY
            DirectionValidationResult.FILENAME_PATTERN_MISMATCH -> DirectionCreationError.FILENAME_PATTERN_MISMATCH
            DirectionValidationResult.CONTENT_IS_EMPTY -> DirectionCreationError.MEDIA_IS_EMPTY
            DirectionValidationResult.CONTENT_IS_TOO_LARGE -> DirectionCreationError.MEDIA_IS_TOO_LARGE
            else -> DirectionCreationError.UNKNOWN_DATABASE_ERROR
        }

    private fun ByteArray.isEqualsTo(other: ByteArray): Boolean =
        this.contentHashCode() == other.contentHashCode() && this.contentEquals(other)
}

enum class DirectionCreationError {
    INVALID_USER_DATA,
    FILENAME_IS_BLANK_OR_EMPTY,
    FILENAME_IS_TOO_LONG,
    FILENAME_PATTERN_MISMATCH,
    MEDIA_IS_EMPTY,
    MEDIA_IS_TOO_LARGE,
    MEDIA_ALREADY_EXIST,
    FILENAME_ALREADY_EXIST,
    UNKNOWN_DATABASE_ERROR,
}
