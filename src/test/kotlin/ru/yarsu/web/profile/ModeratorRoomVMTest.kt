package ru.yarsu.web.profile

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.Status
import ru.yarsu.web.profile.moderator.models.ModeratorRoomVM
import java.time.ZonedDateTime

class ModeratorRoomVMTest : StringSpec({
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
        title = "VeryLongHashtagNameThatNeedsToBeCropped"
    )

    "postsWithHashtag Обрабатывается" {
        val testMap = mapOf(testPost to testHashtag)
        val viewModel = ModeratorRoomVM(testMap)
        viewModel.postsWithHashtag shouldBe testMap
    }

    "Должен генерировать postLink" {
        val viewModel = ModeratorRoomVM(emptyMap())
        viewModel.postLink(testPost) shouldBe "/posts/1"
    }
})
