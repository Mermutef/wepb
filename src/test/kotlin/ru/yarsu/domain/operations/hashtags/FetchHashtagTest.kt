package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.operations.validHashtagTitle

class FetchHashtagTest : FunSpec({
    val validHashtag = Hashtag(
        0,
        validHashtagTitle
    )

    val hashtags = listOf(validHashtag)

    val fetchHashtagByIdMock: (Int) -> Hashtag? = { hashtagID ->
        hashtags.firstOrNull { it.id == hashtagID }
    }

    val fetchHashtagByTitleMock: (String) -> Hashtag? = { title ->
        hashtags.firstOrNull { it.title == title }
    }

    val fetchAllHashtagsMock: () -> List<Hashtag> = { hashtags }

    val fetchHashtagByIDNullMock: (Int) -> Hashtag? = { _ -> null }
    val fetchHashtagByTitleNullMock: (String) -> Hashtag? = { _ -> null }

    val fetchHashtagByID = FetchHashtagById(fetchHashtagByIdMock)
    val fetchHashtagByTitle = FetchHashtagByTitle(fetchHashtagByTitleMock)
    val fetchAllHashtags = FetchAllHashtags(fetchAllHashtagsMock)

    val fetchHashtagByIdNull = FetchHashtagById(fetchHashtagByIDNullMock)
    val fetchHashtagByTitleNull = FetchHashtagByTitle(fetchHashtagByTitleNullMock)

    test("Hashtag can be fetched by his ID") {
        fetchHashtagByID(validHashtag.id).shouldBeSuccess()
    }

    test("Hashtag can be fetched by his title") {
        fetchHashtagByTitle(validHashtag.title).shouldBeSuccess()
    }

    test("FetchAllHashtags should return list of hashtags") {
        fetchAllHashtags().shouldBeSuccess().shouldHaveSize(1)
    }

    listOf(
        validHashtag.id - 1,
        validHashtag.id + 1,
    ).forEach { hashtagID ->
        test("Hashtag can't be fetched by invalid ID == $hashtagID") {
            fetchHashtagByID(hashtagID)
                .shouldBeFailure(HashtagFetchingError.NO_SUCH_HASHTAG)
            fetchHashtagByIdNull(hashtagID)
                .shouldBeFailure(HashtagFetchingError.NO_SUCH_HASHTAG)
        }
    }

    listOf(
        "",
        "    ",
    ).forEach { title ->
        test("Hashtag can't be fetched by invalid title == $title") {
            fetchHashtagByTitle(title)
                .shouldBeFailure(HashtagFetchingError.NO_SUCH_HASHTAG)
            fetchHashtagByTitleNull(title)
                .shouldBeFailure(HashtagFetchingError.NO_SUCH_HASHTAG)
        }
    }
})
