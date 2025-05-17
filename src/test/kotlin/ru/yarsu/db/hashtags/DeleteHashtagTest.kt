package ru.yarsu.db.hashtags

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.TestcontainerSpec
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.validHashtagTitle
import ru.yarsu.domain.models.Hashtag

class DeleteHashtagTest : TestcontainerSpec({ context ->
    val hashtagOperations = HashtagsOperations(context)
    lateinit var insertedHashtag: Hashtag
    beforeEach {
        insertedHashtag =
            hashtagOperations
                .insertHashtag(
                    validHashtagTitle,
                ).shouldNotBeNull()
    }

    test("Hashtag can be delete") {
        hashtagOperations
            .selectAllHashtags()
            .shouldNotBeNull()
            .size
            .shouldBe(1)

        hashtagOperations
            .deleteHashtagById(insertedHashtag.id)
            .shouldBe(1)

        hashtagOperations
            .selectAllHashtags()
            .shouldBeEmpty()
    }

    test("only one hashtag is deleted") {
        hashtagOperations
            .insertHashtag(
                "${validHashtagTitle}2",
            ).shouldNotBeNull()

        hashtagOperations
            .selectAllHashtags()
            .shouldNotBeNull()
            .size
            .shouldBe(2)

        hashtagOperations
            .deleteHashtagById(insertedHashtag.id)
            .shouldBe(1)

        hashtagOperations
            .selectAllHashtags()
            .shouldNotBeNull()
            .size
            .shouldBe(1)
    }

//    test("A hashtag cannot be deleted if it is used in a post entry") {
//        val writer =
//            userOperations
//                .insertUser(
//                    validName,
//                    validUserSurname,
//                    validLogin,
//                    validEmail,
//                    validPhoneNumber,
//                    validPass,
//                    validVKLink,
//                    Role.WRITER,
//                )
//                .shouldNotBeNull()
//
//        val validMedia =
//            mediaOperations
//            .insertMedia(
//                filename = validPostPreview,
//                authorId = writer.id,
//                mediaType = MediaType.VIDEO,
//                content = "Valid content".toByteArray(),
//                birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
//            ).shouldNotBeNull()
//
//        postOperations
//            .insertPost(
//                validPostTitle,
//                validPostPreview,
//                validPostContent,
//                insertedHashtag.id,
//                validPostDate1,
//                validPostDate1,
//                validPostDate1,
//                writer.id,
//                null,
//                Status.DRAFT
//            )
//
//        hashtagOperations
//            .deleteHashtagById(insertedHashtag.id)
//            .shouldBe(null)
//
//        hashtagOperations
//            .selectAllHashtags()
//            .shouldNotBeNull()
//            .size
//            .shouldBe(1)
//    }
})
