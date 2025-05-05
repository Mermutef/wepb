package ru.yarsu.domain.operations

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.yarsu.db.directions.DirectionOperations
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.domain.dependencies.CommentsDatabase
import ru.yarsu.domain.dependencies.DatabaseOperations
import ru.yarsu.domain.dependencies.HashtagsDatabase
import ru.yarsu.domain.dependencies.PostsDatabase
import ru.yarsu.domain.dependencies.UsersDatabase
import ru.yarsu.domain.operations.comments.CommentOperationsHolder
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder
import ru.yarsu.domain.operations.directions.DirectionOperationsHolder
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.domain.operations.posts.PostOperationsHolder
import ru.yarsu.domain.operations.users.UserOperationsHolder

class OperationsHolderTest : FunSpec({
    val mockUserOperations: UsersDatabase = mock()
    val mockDBOperations: DatabaseOperations = mock()
    val mockMediaOperations: MediaOperations = mock()
    val mockPostOperations: PostsDatabase = mock()
    val mockHashtagOperations: HashtagsDatabase = mock()
    val mockDirectionOperations: DirectionOperations = mock()
    val mockCommentOperations: CommentsDatabase = mock()

    whenever(mockDBOperations.userOperations).thenReturn(mockUserOperations)
    whenever(mockDBOperations.mediaOperations).thenReturn(mockMediaOperations)
    whenever(mockDBOperations.postsOperations).thenReturn(mockPostOperations)
    whenever(mockDBOperations.hashtagOperations).thenReturn(mockHashtagOperations)
    whenever(mockDBOperations.directionOperations).thenReturn(mockDirectionOperations)
    whenever(mockDBOperations.commentOperations).thenReturn(mockCommentOperations)

    test("OperationsHolder should initialize with provided user and group operations") {
        val operations = OperationsHolder(mockDBOperations, config)
        operations.userOperations::class shouldBe UserOperationsHolder::class
        operations.mediaOperations::class shouldBe MediaOperationsHolder::class
        operations.postOperations::class shouldBe PostOperationsHolder::class
        operations.hashtagOperations::class shouldBe HashtagOperationsHolder::class
        operations.directionOperations::class shouldBe DirectionOperationsHolder::class
        operations.commentOperations::class shouldBe CommentOperationsHolder::class
    }
})
