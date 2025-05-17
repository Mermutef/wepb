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
    private val selectPostsByHashtagId: (hashtagsId: Int) -> List<Post>,
) : (Hashtag) -> Result4k<Int, HashtagDeleteError> {
    override operator fun invoke(hashtag: Hashtag): Result4k<Int, HashtagDeleteError> {
        try {
            if (selectPostsByHashtagId(hashtag.id).isEmpty()) {
                return Failure(HashtagDeleteError.HASHTAG_USED_IN_POST)
            }
            return when (val countDelete = deleteHashtags(hashtag.id)) {
                is Int -> Success(countDelete)
                else -> Failure(HashtagDeleteError.UNKNOWN_DELETE_ERROR)
            }
        } catch (_: DataAccessException) {
            return Failure(HashtagDeleteError.UNKNOWN_DATABASE_ERROR)
        }
    }
}

enum class HashtagDeleteError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_DELETE_ERROR,
    HASHTAG_USED_IN_POST,
}
