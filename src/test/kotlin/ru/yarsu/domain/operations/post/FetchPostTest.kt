package ru.yarsu.domain.operations.post

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import ru.yarsu.db.validLogin
import ru.yarsu.db.validUserSurname
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.users.FetchAllUsers
import ru.yarsu.domain.operations.users.FetchUserByEmail
import ru.yarsu.domain.operations.users.FetchUserByID
import ru.yarsu.domain.operations.users.FetchUserByLogin
import ru.yarsu.domain.operations.users.FetchUserByPhone
import ru.yarsu.domain.operations.users.FetchUsersByRole
import ru.yarsu.domain.operations.users.UserFetchingError
import ru.yarsu.domain.operations.validEmail
import ru.yarsu.domain.operations.validHashtagTitle
import ru.yarsu.domain.operations.validName
import ru.yarsu.domain.operations.validPass
import ru.yarsu.domain.operations.validPhoneNumber
import ru.yarsu.domain.operations.validPostContent
import ru.yarsu.domain.operations.validPostDate1
import ru.yarsu.domain.operations.validPostPreview
import ru.yarsu.domain.operations.validPostTitle
import ru.yarsu.domain.operations.validVKLink

class FetchPostTest : FunSpec({
    val validHashtag = Hashtag(1, validHashtagTitle)
    val hashtags = listOf(validHashtag)
    val validWriter = User(1,
        validName,
        ru.yarsu.domain.operations.validUserSurname,
        ru.yarsu.domain.operations.validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.WRITER
    )
    val validModerator = User(2,
        validName,
        ru.yarsu.domain.operations.validUserSurname,
        "${ru.yarsu.domain.operations.validLogin}2",
        "$2{validEmail}",
        "79111111111",
        validPass,
        validVKLink,
        Role.MODERATOR
    )
    val users = listOf(validWriter, validModerator)

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

    var postsForFetchByStatus: List<Post> = emptyList()


})