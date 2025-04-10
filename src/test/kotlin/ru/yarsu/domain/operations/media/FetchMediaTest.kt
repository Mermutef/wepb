package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.db.validEmail
import ru.yarsu.db.validLogin
import ru.yarsu.db.validName
import ru.yarsu.db.validPass
import ru.yarsu.db.validPhoneNumber
import ru.yarsu.db.validUserSurname
import ru.yarsu.db.validVKLink
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import ru.yarsu.domain.models.User
import java.time.LocalDateTime

class FetchMediaTest : FunSpec({
    val validStudent = User(
        1,
        validName,
        validUserSurname,
        validLogin,
        validEmail,
        validPhoneNumber,
        validPass,
        validVKLink,
        Role.READER
    )
    val validMedia = MediaFile(
        filename = "test_media.txt",
        content = "Valid content".toByteArray(),
        mediaType = MediaType.VIDEO,
        birthDate = LocalDateTime.of(2025, 1, 16, 17, 41, 28),
        isTemporary = false,
        authorId = validStudent.id,
    )

    val medias = mutableListOf<MediaFile>()

    val fetchMediaWithMetaMock: (String) -> MediaFile? = { filename ->
        medias.firstOrNull { it.filename == filename }
    }
    val fetchOnlyMediaMock: (String) -> ByteArray? = { filename ->
        medias.firstOrNull { it.filename == filename }?.content
    }
    val fetchOnlyMetaMock: (String) -> MediaFile? = { filename ->
        medias.firstOrNull { it.filename == filename }
            ?.copy(content = byteArrayOf())
    }

    val fetchMediaWithMeta = FetchMediaWithMeta(fetchMediaWithMetaMock)
    val fetchOnlyMedia = FetchOnlyMedia(fetchOnlyMediaMock)
    val fetchOnlyMeta = FetchOnlyMeta(fetchOnlyMetaMock)

    beforeEach {
        medias.removeAll(medias)
    }

    test("Fetch media with meta by filename should return this media") {
        medias.add(validMedia)
        val fetchedMedia = fetchMediaWithMeta(validMedia.filename).shouldBeSuccess()

        fetchedMedia.filename shouldBe validMedia.filename
        fetchedMedia.mediaType shouldBe validMedia.mediaType
        fetchedMedia.content.contentEquals(validMedia.content) shouldBe true
        fetchedMedia.authorId shouldBe validMedia.authorId
        fetchedMedia.isTemporary shouldBe validMedia.isTemporary
        fetchedMedia.birthDate shouldBe validMedia.birthDate
    }

    test("Fetch only media by filename should return only media") {
        medias.add(validMedia)
        val fetchedMedia = fetchOnlyMedia(validMedia.filename).shouldBeSuccess()

        fetchedMedia.contentEquals(validMedia.content) shouldBe true
    }

    test("Fetch only meta by filename should return only meta") {
        medias.add(validMedia)
        val fetchedMedia = fetchOnlyMeta(validMedia.filename).shouldBeSuccess()

        fetchedMedia.filename shouldBe validMedia.filename
        fetchedMedia.mediaType shouldBe validMedia.mediaType
        fetchedMedia.content.isEmpty() shouldBe true
        fetchedMedia.authorId shouldBe validMedia.authorId
        fetchedMedia.isTemporary shouldBe validMedia.isTemporary
        fetchedMedia.birthDate shouldBe validMedia.birthDate
    }

    test("Fetch media by filename should return an error if filename is not valid") {
        fetchMediaWithMeta("another filename") shouldBeFailure MediaFetchingError.NO_SUCH_MEDIA
        fetchOnlyMedia("another filename") shouldBeFailure MediaFetchingError.NO_SUCH_MEDIA
        fetchOnlyMeta("another filename") shouldBeFailure MediaFetchingError.NO_SUCH_MEDIA
    }
})
