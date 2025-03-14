package ru.yarsu.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.yarsu.domain.models.MediaFile.Companion.validateMediaData

class MediaFileTest : FunSpec({
    val validMediaFilenames = listOf(
        "Valid_Name-1.0.txt",
        "a".repeat(MediaFile.MAX_FILENAME_LENGTH),
        "Valid_Name-10",
        "some-name",
    )

    val tooLongFilenames = listOf(
        "a".repeat(MediaFile.MAX_FILENAME_LENGTH + 1),
        "b".repeat(MediaFile.MAX_FILENAME_LENGTH + 6),
        "c".repeat(MediaFile.MAX_FILENAME_LENGTH + 23),
    )

    val filenameWithInvalidSymbols = "In\$valid_Name-1.0"
    val filenameWithCyrillicSymbols = "Кирилличекое-имя_файла"
    val filenameWithSpaces = "Name with spaces"

    validMediaFilenames.forEach { filename ->
        test("Should return MediaValidationResult.ALL_OK when filename is $filename and content is not empty") {
            validateMediaData(
                filename,
                "Some content".toByteArray(),
            ) shouldBe MediaValidationResult.ALL_OK
        }
    }

    listOf(
        filenameWithInvalidSymbols,
        filenameWithCyrillicSymbols,
        filenameWithSpaces,
    ).forEach { filename ->
        test("Should return MediaValidationResult.FILENAME_PATTERN_MISMATCH when filename is $filename") {
            validateMediaData(
                filename,
                "Some content".toByteArray(),
            ) shouldBe MediaValidationResult.FILENAME_PATTERN_MISMATCH
        }
    }

    tooLongFilenames.forEach { filename ->
        test("Should return MediaValidationResult.FILENAME_IS_TOO_LONG when filename.length=${filename.length}") {
            validateMediaData(
                filename,
                "Some content".toByteArray(),
            ) shouldBe MediaValidationResult.FILENAME_IS_TOO_LONG
        }
    }

    test("Should return MediaValidationResult.CONTENT_IS_EMPTY when content is empty") {
        validateMediaData(
            "valid_filename",
            byteArrayOf(),
        ) shouldBe MediaValidationResult.CONTENT_IS_EMPTY
    }
})
