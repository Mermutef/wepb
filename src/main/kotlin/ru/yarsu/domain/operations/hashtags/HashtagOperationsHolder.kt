package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.Result4k
import ru.yarsu.domain.dependencies.HashtagsDatabase
import ru.yarsu.domain.dependencies.PostsDatabase
import ru.yarsu.domain.models.Hashtag

class HashtagOperationsHolder(
    private val hashtagsDatabase: HashtagsDatabase,
    private val postsDatabase: PostsDatabase,
) {
    val fetchHashtagById: (Int) -> Result4k<Hashtag, HashtagFetchingError> =
        FetchHashtagById { hashtagID: Int -> hashtagsDatabase.selectHashtagByID(hashtagID) }

    val fetchHashtagByTitle: (String) -> Result4k<Hashtag, HashtagFetchingError> =
        FetchHashtagByTitle { title: String -> hashtagsDatabase.selectHashtagByTitle(title) }

    val fetchAllHashtags: () -> Result4k<List<Hashtag>, HashtagFetchingError> =
        FetchAllHashtags { hashtagsDatabase.selectAllHashtags() }

    val createHashtag: (
        title: String,
    ) -> Result4k<Hashtag, HashtagCreationError> =
        CreateHashtag(
            insertHashtag = { title ->
                hashtagsDatabase.insertHashtag(
                    title = title
                )
            },
            selectHashtagByTitle = { title ->
                hashtagsDatabase.selectHashtagByTitle(
                    title = title
                )
            }
        )

    val changeTitle: (Hashtag, String) -> Result4k<Hashtag, FieldInHashtagChangingError> =
        ChangeHashtagTitle(
            changeTitle = hashtagsDatabase::updateTitle
        )

    val deleteHashtag: (Hashtag) -> Result4k<Int, HashtagDeleteError> =
        DeleteHashtag(
            deleteHashtags = hashtagsDatabase::deleteHashtagById,
            selectPostsByHashtagId = postsDatabase::selectPostsByHashtagId,
            selectHashtagById = hashtagsDatabase::selectHashtagByID
        )
}
