package ru.yarsu.db.hashtags

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.posts.PostsOperations
import ru.yarsu.db.validHashtagTitle
import ru.yarsu.domain.models.Hashtag


class InsertHashtagTest: TestcontainerSpec({ context ->
    val hashtagOperations = HashtagsOperations(context)

    test("Valid hashtag can be inserted") {
        hashtagOperations
            .insertHashtag(
                validHashtagTitle,
            ).shouldNotBeNull()
    }

    test("Valid hashtag insertion should return this hashtag") {
        val insertedHashtag =
            hashtagOperations
                .insertHashtag(
                    validHashtagTitle,
                ).shouldNotBeNull()

        insertedHashtag.title.shouldBe(validHashtagTitle)
    }

    test("Valid hashtag with long title can be inserted") {
        val insertedHashtag =
            hashtagOperations
                .insertHashtag(
                    "a".repeat(
                        Hashtag.MAX_TITLE_LENGTH),
                ).shouldNotBeNull()

        insertedHashtag.title.shouldBe("a".repeat(Hashtag.MAX_TITLE_LENGTH))
    }
})