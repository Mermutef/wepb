package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Hashtag

class FetchHashtagById (
    private val selectHashtagByID: (Int) -> Hashtag?,
) : (Int) -> Result4k<Hashtag, HashtagFetchingError> {
    override operator fun invoke(hashtagId: Int): Result4k<Hashtag, HashtagFetchingError> =
        try {
            when (val hashtag = selectHashtagByID(hashtagId)) {
                is Hashtag -> Success(hashtag)
                else -> Failure(HashtagFetchingError.NO_SUCH_POST)
            }
        } catch (_: DataAccessException) {
            Failure(HashtagFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchHashtagByTitle (
    private val selectHashtagByTitle: (String) -> Hashtag?,
) : (String) -> Result4k<Hashtag, HashtagFetchingError> {
    override operator fun invoke(title: String): Result4k<Hashtag, HashtagFetchingError> =
        try {
            when (val hashtag = selectHashtagByTitle(title)) {
                is Hashtag -> Success(hashtag)
                else -> Failure(HashtagFetchingError.NO_SUCH_POST)
            }
        } catch (_: DataAccessException) {
            Failure(HashtagFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchAllHashtags(
    private val selectAllHashtags: () -> List<Hashtag>,
) : () -> Result4k<List<Hashtag>, HashtagFetchingError> {

    override operator fun invoke(): Result4k<List<Hashtag>, HashtagFetchingError> =
        try {
            Success(selectAllHashtags())
        } catch (_: DataAccessException) {
            Failure(HashtagFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class HashtagFetchingError {
    UNKNOWN_DATABASE_ERROR,
    NO_SUCH_POST,
}
