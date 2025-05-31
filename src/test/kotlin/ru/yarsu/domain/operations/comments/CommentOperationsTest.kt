package ru.yarsu.domain.operations.comments

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.mockito.Mockito.mock
import ru.yarsu.db.comment.CommentOperations
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.posts.PostsOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.domain.operations.hashtags.ChangeHashtagTitle
import ru.yarsu.domain.operations.hashtags.CreateHashtag
import ru.yarsu.domain.operations.hashtags.DeleteHashtag
import ru.yarsu.domain.operations.hashtags.FetchAllHashtags
import ru.yarsu.domain.operations.hashtags.FetchHashtagById
import ru.yarsu.domain.operations.hashtags.FetchHashtagByTitle
import ru.yarsu.domain.operations.hashtags.HashtagOperationsHolder

class CommentOperationsTest : FunSpec({
    val context: DSLContext = mock()

    val userOperations = UserOperations(context)
    val postOperations = PostsOperations(context)
    val commentOperations = CommentOperations(context)
    val commentsOperationsHolder = CommentOperationsHolder(commentOperations, postOperations, userOperations)

    test("CommentOperationsHolder should initialize with provided user operations") {
        commentsOperationsHolder.fetchPublishedCommentsInPost::class.shouldBe(FetchPublishedCommentsInPost::class)
        commentsOperationsHolder.fetchHiddenCommentsInPost::class.shouldBe(FetchHiddenCommentsInPost::class)
        commentsOperationsHolder.fetchHiddenCommentOfUserInPost::class.shouldBe(FetchHiddenCommentsOfUserInPost::class)
        commentsOperationsHolder.createComment::class.shouldBe(CreateComment::class)
        commentsOperationsHolder.changeContent::class.shouldBe(ChangeContentInComment::class)
        commentsOperationsHolder.makeHidden::class.shouldBe(ChangeVisibilityComment::class)
        commentsOperationsHolder.makePublished::class.shouldBe(ChangeVisibilityComment::class)
    }
})