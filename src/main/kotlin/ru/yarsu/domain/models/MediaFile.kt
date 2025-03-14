package ru.yarsu.domain.models

import java.time.LocalDateTime

data class MediaFile(
    val filename: String,
    val authorId: Int,
    val birthDate: LocalDateTime,
    val mediaType: MediaType,
    val content: ByteArray,
    val isTemporary: Boolean = true,
) {
    companion object {
        // 255 - max filename length in symbols (windows, ascii) and bytes (ext4, Unicode) + 1 service byte
        const val MAX_FILENAME_LENGTH = 256
        const val MAX_MEDIA_SIZE_IN_BYTES = 10
        val filenamePattern = Regex("^[\\w-.]+$")
        private const val BYTES_IN_MEGABYTE = 1024 * 1024

        fun validateMediaData(
            filename: String,
            content: ByteArray,
        ): MediaValidationResult =
            when {
                filename.isEmpty() -> MediaValidationResult.FILENAME_IS_BLANK_OR_EMPTY
                filename.length > MAX_FILENAME_LENGTH -> MediaValidationResult.FILENAME_IS_TOO_LONG
                !filenamePattern.matches(filename) -> MediaValidationResult.FILENAME_PATTERN_MISMATCH
                content.isEmpty() -> MediaValidationResult.CONTENT_IS_EMPTY
                content.size > MAX_MEDIA_SIZE_IN_BYTES * BYTES_IN_MEGABYTE -> MediaValidationResult.CONTENT_IS_TOO_LARGE

                else -> MediaValidationResult.ALL_OK
            }
    }
}

enum class MediaValidationResult {
    FILENAME_IS_BLANK_OR_EMPTY,
    FILENAME_IS_TOO_LONG,
    FILENAME_PATTERN_MISMATCH,
    CONTENT_IS_EMPTY,
    CONTENT_IS_TOO_LARGE,
    ALL_OK,
}

enum class MediaType(val pattern: Regex) {
    IMAGE(pattern = """<image:(.*?)>""".toRegex()),
    SOUND(pattern = """<sound:(.*?)>""".toRegex()),
    VIDEO(pattern = """<video:(.*?)>""".toRegex()),
}
