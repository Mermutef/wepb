package ru.yarsu.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.operations.validPostContent
import ru.yarsu.domain.operations.validPostPreview
import ru.yarsu.domain.operations.validPostTitle
import java.time.ZonedDateTime

class PostTest : FunSpec({

    test("blank title") {
        Post.validateTitle("  \t\n") shouldBe PostValidationResult.TITLE_IS_BLANK_OR_EMPTY
    }
    test("too long title") {
        Post.validateTitle("a".repeat(Post.MAX_TITLE_LENGTH + 1)) shouldBe PostValidationResult.TITLE_IS_TOO_LONG
    }
    test("valid title") {
        Post.validateTitle(validPostTitle) shouldBe null
    }
    test("blank preview") {
        Post.validatePreview("   \t\n") shouldBe PostValidationResult.PREVIEW_IS_BLANK_OR_EMPTY
    }
    test("valid preview") {
        Post.validatePreview(validPostPreview) shouldBe null
    }
    test("blank content") {
        Post.validateContent("  \t\n") shouldBe PostValidationResult.CONTENT_IS_BLANK_OR_EMPTY
    }
    test("valid content") {
        Post.validateContent(validPostContent)
    }
    test("valid Post") {
        Post(
            1,
            validPostTitle,
            validPostPreview,
            validPostContent,
            1,
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            1,
            2,
            Status.DRAFT
        )

        Post.validatePostData(
            validPostTitle,
            validPostPreview,
            validPostContent,
        ) shouldBe PostValidationResult.ALL_OK
    }
})
