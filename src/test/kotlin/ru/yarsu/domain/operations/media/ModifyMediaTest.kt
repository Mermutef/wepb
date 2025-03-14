package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import ru.yarsu.db.validEmail
import ru.yarsu.db.validPass
import ru.yarsu.db.validUserName
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class ModifyMediaTest : FunSpec({
    val validStudent = User(1, validUserName, validEmail, validPass, Role.AUTHORIZED)
    val validMedia = MediaFile(
        filename = "test_media.txt",
        content = "Valid content".toByteArray(),
        mediaType = MediaType.VIDEO,
        birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
        isTemporary = false,
        authorId = validStudent.id,
    )

    val medias = mutableListOf<MediaFile>()

    val makeMediaTemporaryMock: (String) -> MediaFile? = { filename ->
        medias.firstOrNull { it.filename == filename }?.let {
            medias.remove(it)
            medias.add(it.copy(isTemporary = true))
            return@let medias.firstOrNull { media -> media.filename == filename }
        }
    }

    val makeMediaRegularMock: (String) -> MediaFile? = { filename ->
        medias.firstOrNull { it.filename == filename }?.let {
            medias.remove(it)
            medias.add(it.copy(isTemporary = false))
            return@let medias.firstOrNull { media -> media.filename == filename }
        }
    }

    val removeMediaMock: (String) -> Unit = { filename -> medias.removeIf { it.filename == filename } }

    val makeMediaTemporary = MakeMediaTemporary(makeMediaTemporaryMock)
    val makeMediaRegular = MakeMediaRegular(makeMediaRegularMock)

    val removeMedia = RemoveMedia(removeMediaMock)

    beforeEach {
        medias.removeAll(medias)
    }

    test("Media cannot be marked as temporary if it is temporary") {
        val anotherValidMedia = validMedia.copy(
            filename = validMedia.filename,
            isTemporary = true,
        )
        medias.add(anotherValidMedia)
        makeMediaTemporary(anotherValidMedia) shouldBeFailure MakeMediaTemporaryOrRegularError.NOTHING_TO_CHANGE
    }

    test("Media cannot be marked as regular if it is regular") {
        val anotherValidMedia = validMedia.copy(
            filename = validMedia.filename,
            isTemporary = false,
        )
        medias.add(anotherValidMedia)
        makeMediaRegular(anotherValidMedia) shouldBeFailure MakeMediaTemporaryOrRegularError.NOTHING_TO_CHANGE
    }

    test("Media can be marked as temporary") {
        val anotherValidMedia = validMedia.copy(
            filename = validMedia.filename,
            isTemporary = false,
        )
        medias.add(anotherValidMedia)
        makeMediaTemporary(anotherValidMedia).shouldBeSuccess()
    }

    test("Media can be marked as regular") {
        val anotherValidMedia = validMedia.copy(
            filename = validMedia.filename,
            isTemporary = true,
        )
        medias.add(anotherValidMedia)
        makeMediaRegular(anotherValidMedia).shouldBeSuccess()
    }

    test("Null as a result of makeMediaRegular should result in an error") {
        makeMediaTemporary(validMedia.copy(isTemporary = false)) shouldBeFailure
            MakeMediaTemporaryOrRegularError.UNKNOWN_CHANGING_ERROR
    }

    test("Null as a result of makeMediaTemporary should result in an error") {
        makeMediaRegular(validMedia.copy(isTemporary = true)) shouldBeFailure
            MakeMediaTemporaryOrRegularError.UNKNOWN_CHANGING_ERROR
    }

    test("Remove media should return success if filename is valid") {
        medias.add(validMedia)
        removeMedia(validMedia).shouldBeSuccess()
    }
})
