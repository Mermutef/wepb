package ru.yarsu.domain.operations.post

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.mockito.Mockito.mock
import ru.yarsu.db.hashtag.HashtagsOperations
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.db.posts.PostsOperations
import ru.yarsu.db.users.UserOperations
import ru.yarsu.domain.operations.posts.ChangeDateFieldInPost
import ru.yarsu.domain.operations.posts.ChangeHashtagIdInPost
import ru.yarsu.domain.operations.posts.ChangePost
import ru.yarsu.domain.operations.posts.ChangePreviewInPost
import ru.yarsu.domain.operations.posts.ChangeStringFieldInPost
import ru.yarsu.domain.operations.posts.ChangeUserIdInPost
import ru.yarsu.domain.operations.posts.CreatePost
import ru.yarsu.domain.operations.posts.FetchAllPosts
import ru.yarsu.domain.operations.posts.FetchNNewestPosts
import ru.yarsu.domain.operations.posts.FetchPostById
import ru.yarsu.domain.operations.posts.FetchPostByStatus
import ru.yarsu.domain.operations.posts.FetchPostsByAuthorId
import ru.yarsu.domain.operations.posts.FetchPostsByIdHashtag
import ru.yarsu.domain.operations.posts.FetchPostsByModeratorId
import ru.yarsu.domain.operations.posts.FetchPostsByTimeInterval
import ru.yarsu.domain.operations.posts.PostOperationsHolder
import ru.yarsu.domain.operations.posts.StatusChanger

class PostsOperationsHolderTest : FunSpec({
    val context: DSLContext = mock()

    val hashtagOperations = HashtagsOperations(context)
    val postOperations = PostsOperations(context)
    val mediaOperations = MediaOperations(context)
    val userOperations = UserOperations(context)
    val postOperationsHolder = PostOperationsHolder(postOperations, hashtagOperations, userOperations, mediaOperations)

    test("PostOperationsHolder should initialize with provided user operations") {
        postOperationsHolder.fetchAllPosts::class.shouldBe(FetchAllPosts::class)
        postOperationsHolder.fetchPostById::class.shouldBe(FetchPostById::class)
        postOperationsHolder.fetchPostsByHashtagId::class.shouldBe(FetchPostsByIdHashtag::class)
        postOperationsHolder.fetchNNewestPosts::class.shouldBe(FetchNNewestPosts::class)
        postOperationsHolder.fetchPostsByAuthorId::class.shouldBe(FetchPostsByAuthorId::class)
        postOperationsHolder.fetchPostsByModeratorId::class.shouldBe(FetchPostsByModeratorId::class)
        postOperationsHolder.fetchPostsByStatus::class.shouldBe(FetchPostByStatus::class)
        postOperationsHolder.fetchPostsByTimeInterval::class.shouldBe(FetchPostsByTimeInterval::class)
        postOperationsHolder.createPost::class.shouldBe(CreatePost::class)
        postOperationsHolder.changeTitle::class.shouldBe(ChangeStringFieldInPost::class)
        postOperationsHolder.changePreview::class.shouldBe(ChangePreviewInPost::class)
        postOperationsHolder.changeContent::class.shouldBe(ChangeStringFieldInPost::class)
        postOperationsHolder.changeHashtagId::class.shouldBe(ChangeHashtagIdInPost::class)
        postOperationsHolder.changeEventDate::class.shouldBe(ChangeDateFieldInPost::class)
        postOperationsHolder.changeAuthorId::class.shouldBe(ChangeUserIdInPost::class)
        postOperationsHolder.changeModeratorId::class.shouldBe(ChangeUserIdInPost::class)
        postOperationsHolder.makePublished::class.shouldBe(StatusChanger::class)
        postOperationsHolder.makeHidden::class.shouldBe(StatusChanger::class)
        postOperationsHolder.makeModeration::class.shouldBe(StatusChanger::class)
        postOperationsHolder.makeDraft::class.shouldBe(StatusChanger::class)
        postOperationsHolder.changePost::class.shouldBe(ChangePost::class)
    }
})
