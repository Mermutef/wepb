package ru.yarsu.db.hashtags

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.validHashtagTitle
import ru.yarsu.domain.models.Hashtag

class UpdateHashtagTest: TestcontainerSpec({ context ->
    val hashtagOperations = HashtagsOperations(context)
    lateinit var insertedHashtag: Hashtag
    beforeEach {
        insertedHashtag =
            hashtagOperations
                .insertHashtag(
                    validHashtagTitle,
                ).shouldNotBeNull()
    }

    test("Hashtag title can be changed") {
        val newTitle = "${validHashtagTitle}2"
        hashtagOperations
            .updateTitle(insertedHashtag.id, newTitle)
            .shouldNotBeNull().title shouldBe newTitle
    }
})