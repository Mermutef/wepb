package ru.yarsu.domain.operations.hashtags

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.operations.validHashtagTitle

class ModifyHashtagTest : FunSpec({
    val validHashtag = Hashtag(
        1,
        validHashtagTitle
    )

    val changeTitleMock: (hashtagID: Int, newTitle: String) -> Hashtag? =
        { _, newTitle -> validHashtag.copy(title = newTitle) }

    val changeTitle = ChangeHashtagTitle(changeTitleMock)

    val changeTitleNullMock: (hashtagID: Int, newTitle: String) -> Hashtag? =
        { _, _ -> null }

    val changeTitleNull = ChangeHashtagTitle(changeTitleNullMock)

    test("Title can be changed to valid title") {
        changeTitle(validHashtag, "${validHashtagTitle}2").shouldBeSuccess().title shouldBe
            "${validHashtagTitle}2"
    }

    test("Title cannot be changed to blank title") {
        changeTitle(validHashtag, "  \t\n") shouldBeFailure
            FieldInHashtagChangingError.FIELD_IS_BLANK_OR_EMPTY
    }

    test("Title cannot be changed too long title") {
        changeTitle(validHashtag, "a".repeat(Hashtag.MAX_TITLE_LENGTH + 1)) shouldBeFailure
            FieldInHashtagChangingError.FIELD_IS_TOO_LONG
    }

    test("Unknown db error test for changeTitle") {
        changeTitleNull(validHashtag, "${validHashtagTitle}2") shouldBeFailure
            FieldInHashtagChangingError.UNKNOWN_CHANGING_ERROR
    }
})
