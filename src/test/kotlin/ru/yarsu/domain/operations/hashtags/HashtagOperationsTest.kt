package ru.yarsu.domain.operations.hashtags

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.mockito.Mockito.mock
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.posts.PostsOperations

class HashtagOperationsTest : FunSpec({
    val context: DSLContext = mock()

    val hashtagOperations = HashtagsOperations(context)
    val postOperations = PostsOperations(context)
    val hashtagOperationsHolder = HashtagOperationsHolder(hashtagOperations, postOperations)

    test("HashtagOperationsHolder should initialize with provided user operations") {
        hashtagOperationsHolder.fetchHashtagById::class.shouldBe(FetchHashtagById::class)
        hashtagOperationsHolder.fetchHashtagByTitle::class.shouldBe(FetchHashtagByTitle::class)
        hashtagOperationsHolder.fetchAllHashtags::class.shouldBe(FetchAllHashtags::class)
        hashtagOperationsHolder.createHashtag::class.shouldBe(CreateHashtag::class)
        hashtagOperationsHolder.changeTitle::class.shouldBe(ChangeHashtagTitle::class)
        hashtagOperationsHolder.deleteHashtag::class.shouldBe(DeleteHashtag::class)
    }
})
