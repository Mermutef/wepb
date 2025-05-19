package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Hashtag

class ChangeTitleInHashtag(
    private val changeTitle: (hashtagID: Int, newTitle: String) -> Hashtag?,
) : (Hashtag, String) -> Result4k<Hashtag, FieldInHashtagChangingError> {
    override operator fun invoke(
        hashtag: Hashtag,
        newTitle: String,
    ): Result4k<Hashtag, FieldInHashtagChangingError> =
        try {
            when {
                newTitle.isBlank() ->
                    Failure(FieldInHashtagChangingError.FIELD_IS_BLANK_OR_EMPTY)
                newTitle.length > Hashtag.MAX_TITLE_LENGTH ->
                    Failure(FieldInHashtagChangingError.FIELD_IS_TOO_LONG)
                else -> when (val hashtagWithNewTitle = changeTitle(hashtag.id, newTitle)) {
                    is Hashtag -> Success(hashtagWithNewTitle)
                    else -> Failure(FieldInHashtagChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(FieldInHashtagChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class FieldInHashtagChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    FIELD_IS_BLANK_OR_EMPTY,
    FIELD_IS_TOO_LONG,
}
