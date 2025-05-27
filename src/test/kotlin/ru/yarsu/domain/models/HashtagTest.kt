package ru.yarsu.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.operations.validHashtagTitle

class HashtagTest : FunSpec({
    test("blank title") {
        Hashtag.validateTitle("  \t\n") shouldBe HashtagValidationResult.TITLE_IS_BLANK_OR_EMPTY
    }
    test("too long title") {
        Hashtag.validateTitle("a".repeat(Hashtag.MAX_TITLE_LENGTH + 1)) shouldBe HashtagValidationResult
            .TITLE_IS_TOO_LONG
    }
    test("valid title") {
        Hashtag.validateTitle(validHashtagTitle) shouldBe null
    }
    test("valid hashtag") {
        Hashtag(
            1,
            validHashtagTitle
        )
        Hashtag.validateHashtagData(validHashtagTitle) shouldBe HashtagValidationResult.ALL_OK
    }
})
