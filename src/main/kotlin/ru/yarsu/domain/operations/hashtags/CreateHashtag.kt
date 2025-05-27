package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.HashtagValidationResult

class CreateHashtag(
    private val insertHashtag: (title: String) -> Hashtag?,
    private val selectHashtagByTitle: (title: String) -> Hashtag?,
) : (String) -> Result4k<Hashtag, HashtagCreationError> {
    override operator fun invoke(title: String): Result4k<Hashtag, HashtagCreationError> =
        when {
            Hashtag.validateHashtagData(title) != HashtagValidationResult.ALL_OK ->
                Failure(HashtagCreationError.INVALID_HASHTAG_DATA)
            titleAlreadyExists(title) ->
                Failure(HashtagCreationError.HASHTAG_ALREADY_EXISTS)
            else ->
                when (
                    val newHashtag = insertHashtag(title)
                ) {
                    is Hashtag -> Success(newHashtag)
                    else -> Failure(HashtagCreationError.UNKNOWN_DATABASE_ERROR)
                }
        }

    private fun titleAlreadyExists(title: String): Boolean =
        when (selectHashtagByTitle(title)) {
            is Hashtag -> true
            else -> false
        }
}

enum class HashtagCreationError {
    HASHTAG_ALREADY_EXISTS,
    INVALID_HASHTAG_DATA,
    UNKNOWN_DATABASE_ERROR,
}
