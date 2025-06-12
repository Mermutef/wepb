package ru.yarsu.web.profile

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.web.profile.writer.models.WriterRoomVM
import java.time.ZonedDateTime

class WriterRoomVMTest : StringSpec({
    val testPost = Post(
        id = 1,
        title = "Test Post",
        preview = "Preview",
        content = "Content",
        hashtagId = 1,
        eventDate = ZonedDateTime.now(),
        creationDate = ZonedDateTime.now(),
        lastModifiedDate = ZonedDateTime.now(),
        authorId = 1,
        moderatorId = 1,
        status = Status.DRAFT
    )

    val testHashtag = Hashtag(
        id = 1,
        title = "VeryLongHashtagNameThatNeedsToBeCroppedOneTwoThree"
    )

    val testPostsMap = mapOf(testPost to testHashtag)
    val viewModel = WriterRoomVM(testPostsMap)

    "Генерируется ссылка на пост" {
        viewModel.postLink(testPost) shouldBe "/posts/1"
    }

    "postsWithHashtag должен содержать предоставленные данные" {
        viewModel.postsWithHashtag shouldBe testPostsMap
        viewModel.postsWithHashtag[testPost] shouldBe testHashtag
    }
})
