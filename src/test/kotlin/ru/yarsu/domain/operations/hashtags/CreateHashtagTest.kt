package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.operations.validHashtagTitle

class CreateHashtagTest : FunSpec({
    val hashtags = mutableListOf<Hashtag>()

    beforeEach {
        hashtags.clear()
    }

    val insertHashtagMock: (
        title: String,
    ) ->
    Hashtag? = { title ->
        val hashtag =
            Hashtag(
                id = hashtags.size + 1,
                title
            )
        hashtags.add(hashtag)
        hashtag
    }

    val fetchHashtagByTitleMock: (String) -> Hashtag? = { hashtagTitle ->
        hashtags.firstOrNull { it.title == hashtagTitle }
    }

    val insertHashtagNullMock: (
        title: String,
    ) -> Hashtag? = { _ -> null }

    val fetchHashtagByTitleNullMock: (String) -> Hashtag? = { _ -> null }

    val createHashtag = CreateHashtag(
        insertHashtagMock,
        fetchHashtagByTitleMock
    )

    val createHashtagNullTitle = CreateHashtag(
        insertHashtagNullMock,
        fetchHashtagByTitleNullMock
    )

    test("Valid hashtag can be inserted") {
        createHashtag(
            validHashtagTitle
        )
            .shouldBeSuccess()
    }

    listOf(
        "",
        "    ",
        "a".repeat(Hashtag.MAX_TITLE_LENGTH + 1),
    ).forEach { invalidTitle ->
        test("Hashtag with invalid title should not be inserted ($invalidTitle)") {
            createHashtag(
                invalidTitle
            ).shouldBeFailure(HashtagCreationError.INVALID_HASHTAG_DATA)
        }
    }

    test("There cannot be two hashtags with the same title") {
        createHashtag(
            validHashtagTitle
        ).shouldBeSuccess()

        createHashtag(
            validHashtagTitle
        ).shouldBeFailure(HashtagCreationError.HASHTAG_ALREADY_EXISTS)
    }

    test("Unknown db error test for CreateHashtag") {
        createHashtagNullTitle(
            validHashtagTitle
        ).shouldBeFailure(HashtagCreationError.UNKNOWN_DATABASE_ERROR)
    }
})
