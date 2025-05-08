package ru.yarsu.db.hashtags

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.appConfiguredPasswordHasher
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.validEmail
import ru.yarsu.db.validHashtagTitle
import ru.yarsu.db.validLogin
import ru.yarsu.db.validName
import ru.yarsu.db.validPass
import ru.yarsu.db.validPhoneNumber
import ru.yarsu.db.validSecondPhoneNumber
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.User

class SelectHashtagTest: TestcontainerSpec({ context ->
    val hashtagOperations = HashtagsOperations(context)
    lateinit var insertedHashtag: Hashtag
    beforeEach {
        insertedHashtag =
            hashtagOperations
                .insertHashtag(
                    validHashtagTitle,
                ).shouldNotBeNull()
    }

    test("There is only hashtag by default") {
        hashtagOperations
            .selectAllHashtags()
            .shouldNotBeNull()
            .size
            .shouldBe(1)
    }

    test("If two hashtags were added") {

        // already added one

        hashtagOperations
            .insertHashtag(
                "${validHashtagTitle}2",
            )

        hashtagOperations
            .selectAllHashtags()
            .shouldNotBeNull()
            .shouldHaveSize(2)
    }

    test("Hashtag can be fetched by valid ID") {
        val fetchedHashtag =
            hashtagOperations
                .selectHashtagByID(insertedHashtag.id)
                .shouldNotBeNull()

        fetchedHashtag.id.shouldBe(insertedHashtag.id)
        fetchedHashtag.title.shouldBe(insertedHashtag.title)
    }

    test("Hashtag can be fetched by valid title") {
        val fetchedHashtag =
            hashtagOperations
                .selectHashtagByTitle(insertedHashtag.title)
                .shouldNotBeNull()

        fetchedHashtag.id.shouldBe(insertedHashtag.id)
        fetchedHashtag.title.shouldBe(insertedHashtag.title)
    }

    test("Hashtag can't be fetched by invalid ID") {
        hashtagOperations
            .selectHashtagByID(Int.MIN_VALUE)
            .shouldBeNull()
    }

    listOf(
        "",
        "a".repeat(Hashtag.MAX_TITLE_LENGTH + 1),
    ).forEach { invalidTitle ->
        test("Hashtag can't be fetched by invalid title (this: $invalidTitle)") {
            hashtagOperations.selectHashtagByTitle(invalidTitle).shouldBeNull()
        }
    }
})