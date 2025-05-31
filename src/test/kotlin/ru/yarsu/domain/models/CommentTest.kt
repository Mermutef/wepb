package ru.yarsu.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.operations.validCommentContent
import ru.yarsu.domain.operations.validHashtagTitle
import ru.yarsu.domain.operations.validPostDate2

class CommentTest : FunSpec({
    test("blank content") {
        Comment.validateContent("  \t\n") shouldBe CommentValidationResult.CONTENT_IS_BLANK_OR_EMPTY
    }
    test("too long content") {
        Comment.validateContent("a".repeat(Comment.MAX_CONTENT_LENGTH + 1)) shouldBe CommentValidationResult
            .CONTENT_IS_TOO_LONG
    }
    test("valid content") {
        Comment.validateContent(validCommentContent) shouldBe null
    }
    test("valid comment") {
        Comment(
            1,
            validCommentContent,
            1,
            1,
            validPostDate2,
            validPostDate2,
            false
        )
        Comment.validateCommentData(validCommentContent) shouldBe CommentValidationResult.ALL_OK
    }
})