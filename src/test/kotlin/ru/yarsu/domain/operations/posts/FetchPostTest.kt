package ru.yarsu.domain.operations.posts

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validHashtagTitle
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validPostContent
import ru.yarsu.domain.operations.validPostDate1
import ru.yarsu.domain.operations.validPostDate2
import ru.yarsu.domain.operations.validPostPreview
import ru.yarsu.domain.operations.validPostTitle
import ru.yarsu.domain.operations.validVKLink
import java.time.ZonedDateTime

class FetchPostTest : FunSpec({
    val validHashtag = Hashtag(1, validHashtagTitle)
    val validWriter = User(
        1,
        validName,
        ru.yarsu.domain.operations.validUserSurname,
        ru.yarsu.domain.operations.validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.WRITER
    )
    val validModerator = User(
        2,
        validName,
        ru.yarsu.domain.operations.validUserSurname,
        "${ru.yarsu.domain.operations.validLogin}2",
        "$2{validEmail}",
        "79111111111",
        validPass,
        validVKLink,
        Role.MODERATOR
    )
    val validPost = Post(
        1,
        validPostTitle,
        validPostPreview,
        validPostContent,
        validHashtag.id,
        validPostDate1,
        validPostDate1,
        validPostDate1,
        validWriter.id,
        validModerator.id,
        Status.DRAFT
    )
    val posts = listOf(validPost)
    val fetchPostByIdMock: (Int) -> Post? = { postId ->
        posts.firstOrNull { it.id == postId }
    }
    val fetchPostsByHashtagIdMock: (Int) -> List<Post> = { hashtagId ->
        posts.filter { it.hashtagId == hashtagId }
    }
    val fetchNNewPostsMock: (Int) -> List<Post> = { countN ->
        posts.sortedByDescending { it.creationDate }.take(countN)
    }
    val fetchPostsByAuthorIdMock: (Int) -> List<Post> = { authorId ->
        posts.filter { it.authorId == authorId }
    }
    val fetchPostsByModeratorIdMock: (Int) -> List<Post> = { moderatorId ->
        posts.filter { it.moderatorId == moderatorId }
    }
    val fetchPostsByStatusMock: (Status) -> List<Post> = { status ->
        posts.filter { it.status == status }
    }
    val fetchPostsByTimeIntervalMock: (ZonedDateTime, ZonedDateTime) -> List<Post> = { startDate, endDate ->
        posts.filter { it.creationDate in startDate..endDate }
    }
    val fetchAllPostsMock: () -> List<Post> = { posts }
    val fetchPostByIdNullMock: (Int) -> Post? = { _ -> null }

    val fetchPostById = FetchPostById(fetchPostByIdMock)
    val fetchPostsByHashtagId = FetchPostsByIdHashtag(fetchPostsByHashtagIdMock)
    val fetchNNewPosts = FetchNNewestPosts(fetchNNewPostsMock)
    val fetchPostsByAuthorId = FetchPostsByAuthorId(fetchPostsByAuthorIdMock)
    val fetchPostsByModerator = FetchPostsByModeratorId(fetchPostsByModeratorIdMock)
    val fetchPostsByStatus = FetchPostByStatus(fetchPostsByStatusMock)
    val fetchPostsByTimeInterval = FetchPostsByTimeInterval(fetchPostsByTimeIntervalMock)
    val fetchAllPosts = FetchAllPosts(fetchAllPostsMock)
    val fetchPostByIdNull = FetchPostById(fetchPostByIdNullMock)

    test("Post can be fetched by his ID") {
        fetchPostById(validPost.id).shouldBeSuccess()
    }

    test("Posts can be fetched by hashtagId") {
        fetchPostsByHashtagId(validPost.hashtagId).shouldBeSuccess()
    }

    test("New n posts can be fetched") {
        fetchNNewPosts(posts.size).shouldBeSuccess()
    }

    test("Posts can be fetched by authorId") {
        fetchPostsByAuthorId(validPost.authorId).shouldBeSuccess()
    }

    test("Posts can be fetched by moderatorId") {
        validPost.moderatorId?.let { fetchPostsByModerator(it).shouldBeSuccess() }
    }

    test("Posts can be fetched by status") {
        fetchPostsByStatus(validPost.status).shouldBeSuccess()
    }

    test("Posts can be fetched by time interval") {
        fetchPostsByTimeInterval(validPostDate1, validPostDate2).shouldBeSuccess()
    }

    test("All posts can be fetched") {
        fetchAllPosts().shouldBeSuccess()
    }

    test("Post can be fetched by his ID") {
        fetchPostById(validPost.id).shouldBeSuccess()
    }

    test("Post can not be fetched by invalid ID") {
        fetchPostById(Int.MIN_VALUE)
            .shouldBeFailure(PostFetchingError.NO_SUCH_POST)
        fetchPostByIdNull(Int.MIN_VALUE)
            .shouldBeFailure(PostFetchingError.NO_SUCH_POST)
    }
})
