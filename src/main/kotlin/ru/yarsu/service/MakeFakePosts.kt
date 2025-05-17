package ru.yarsu.service

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import ru.yarsu.domain.accounts.Status
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.domain.operations.posts.PostCreationError
import ru.yarsu.domain.operations.posts.PostsOperationsHolder
import ru.yarsu.web.profile.writer.handlers.CreationPostError
import java.io.File
import java.time.LocalDateTime
import java.time.ZonedDateTime

fun createPost(
    postsOperations: PostsOperationsHolder,
    writer: User,
    preview: String,
): Result<Post, CreationPostError> {
    return when (
        val post = postsOperations.createPost(
            "NewYear",
            preview,
            "Поздравляем всех с наступившим Новым 2025 годом! Новогоднее профбюро ИВТ желает всем " +
                "счастья в новом году и успехов в учебе! Пусть этот год принесет много радости " +
                "всей большой семье факультета ИВТ. P.S. Новогодний Шлёпа",
            1,
            ZonedDateTime.parse("2025-06-15T15:00:00+03:00"),
            writer.id,
            null,
            Status.DRAFT
        )
    ) {
        is Failure -> when (post.reason) {
            PostCreationError.UNKNOWN_DATABASE_ERROR -> Failure(CreationPostError.UNKNOWN_DATABASE_ERROR)
            PostCreationError.INVALID_POST_DATA -> Failure(CreationPostError.INVALID_POST_DATA)
            PostCreationError.HASHTAG_NOT_EXISTS -> Failure(CreationPostError.HASHTAG_NOT_EXISTS)
        }

        is Success -> Success(post.value)
    }
}

fun createMedia(
    mediaOperations: MediaOperationsHolder,
    writer: User,
    file: File,
): String? {
    return when (
        val createdMedia = mediaOperations.createMedia(
            "Shlepa",
            writer,
            MediaType.IMAGE,
            LocalDateTime.now(),
            file.readBytes(),
            true,
        )
    ) {
        is Success -> createdMedia.value.filename

        is Failure -> null
    }
}
