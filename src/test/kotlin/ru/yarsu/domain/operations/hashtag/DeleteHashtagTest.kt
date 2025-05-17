package ru.yarsu.domain.operations.hashtag

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.operations.hashtags.DeleteHashtags
import ru.yarsu.domain.operations.hashtags.HashtagDeleteError
import ru.yarsu.domain.operations.validHashtagTitle
import ru.yarsu.domain.operations.validPostTitle
import ru.yarsu.domain.operations.validPostPreview
import ru.yarsu.domain.operations.validPostContent
import ru.yarsu.domain.operations.validPostDate1

class DeleteHashtagTest : FunSpec({
    val validHashtag = Hashtag(
        0,
        validHashtagTitle
    )

    val invalidHashtag = Hashtag(
        10,
        validHashtagTitle
    )

    val hashtags = mutableListOf<Hashtag>()

    val validPostWithHashtag = Post(
        1,
        validPostTitle,
        validPostPreview,
        validPostContent,
        0,
        validPostDate1,
        validPostDate1,
        validPostDate1,
        1,
        2,
        Status.DRAFT
    )

    val validPostWithoutHashtag = Post(
        1,
        validPostTitle,
        validPostPreview,
        validPostContent,
        1,
        validPostDate1,
        validPostDate1,
        validPostDate1,
        1,
        2,
        Status.DRAFT
    )

    val postsWithHashtag = listOf(validPostWithHashtag)
    val postsWithoutHashtag = listOf(validPostWithoutHashtag)

    beforeEach{
        hashtags.clear()
        hashtags.add(validHashtag)
    }

    val fetchPostWithHashtagByIdMock: (Int) -> List<Post> = { hashtagID ->
        postsWithHashtag.filter { it.hashtagId == hashtagID }
    }

    val fetchPostWithoutHashtagByIdMock: (Int) -> List<Post> = { hashtagID ->
        postsWithoutHashtag.filter { it.hashtagId == hashtagID }
    }

    val fetchHashtagById: (Int) -> Hashtag? = {hashtagId ->
        hashtags.firstOrNull { it.id == hashtagId }
    }

    val deleteHashtagByIdMock: (Int) -> Int? = { hashtagId ->
        val initialSize = hashtags.size
        hashtags.removeAll { it.id == hashtagId }
        initialSize - hashtags.size
    }

    val deleteHashtagByIdNullMock: (Int) -> Int? = { _ -> null}

    val deleteHashtag = DeleteHashtags(deleteHashtagByIdMock, fetchPostWithoutHashtagByIdMock, fetchHashtagById)

    val deleteHashtagInPost = DeleteHashtags(deleteHashtagByIdMock, fetchPostWithHashtagByIdMock, fetchHashtagById)

    val deleteHashtagNull = DeleteHashtags(deleteHashtagByIdNullMock, fetchPostWithoutHashtagByIdMock, fetchHashtagById)

    test("Hashtag can be delete")
    {
        deleteHashtag(validHashtag).shouldBeSuccess(1)
        hashtags.size.shouldBe(0)
    }

    test("Hashtag can not be delete if hashtag used in post")
    {
        deleteHashtagInPost(validHashtag).shouldBeFailure(HashtagDeleteError.HASHTAG_USED_IN_POST)
    }

    test("Hashtag can not be delete if hashtag id not exists")
    {
        deleteHashtag(invalidHashtag).shouldBeFailure(HashtagDeleteError.HASHTAG_NOT_EXISTS)
    }

    test("unknown delete error")
    {
        deleteHashtagNull(validHashtag).shouldBeFailure(HashtagDeleteError.UNKNOWN_DELETE_ERROR)
    }
})