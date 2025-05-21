package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post

@Suppress("detekt:ReturnCount")
class DeleteHashtags(
    private val deleteHashtags: (hashtagID: Int) -> Int?,
    private val selectPostsByHashtagId: (hashtagId: Int) -> List<Post>,
    private val selectHashtagById: (hashtagId: Int) -> Hashtag?,
) : (Hashtag) -> Result4k<Int, HashtagDeleteError> {
    override operator fun invoke(hashtag: Hashtag): Result4k<Int, HashtagDeleteError> {
        try {
            if (selectPostsByHashtagId(hashtag.id).isNotEmpty()) {
                return Failure(HashtagDeleteError.HASHTAG_USED_IN_POST)
            }
            if (hashtagNotExists(hashtag.id)) {
                return Failure(HashtagDeleteError.HASHTAG_NOT_EXISTS)
            }
            return when (val countDelete = deleteHashtags(hashtag.id)) {
                is Int -> Success(countDelete)
                else -> Failure(HashtagDeleteError.UNKNOWN_DELETE_ERROR)
            }
        } catch (_: DataAccessException) {
            return Failure(HashtagDeleteError.UNKNOWN_DATABASE_ERROR)
        }
    }

    private fun hashtagNotExists(hashtagId: Int): Boolean =
        when (selectHashtagById(hashtagId)) {
            is Hashtag -> false
            else -> true
        }
}

enum class HashtagDeleteError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_DELETE_ERROR,
    HASHTAG_USED_IN_POST,
    HASHTAG_NOT_EXISTS,
}
