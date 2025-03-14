package ru.yarsu.db.media

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.enums.ContentType
import ru.yarsu.db.generated.tables.Media.Companion.MEDIA
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.dependencies.MediaDatabase
import ru.yarsu.domain.models.MediaFile
import ru.yarsu.domain.models.MediaType
import java.time.LocalDateTime

const val REMOVING_TEMP_FILES_LIMIT_IN_DAYS = 7L

class MediaOperations(private val jooqContext: DSLContext) : MediaDatabase {
    override fun insertMedia(
        filename: String,
        authorId: Int,
        mediaType: MediaType,
        birthDate: LocalDateTime,
        content: ByteArray,
        isTemporary: Boolean,
    ): MediaFile? {
        if (isTemporary) {
            deleteAllOldTempMedia()
        }
        return jooqContext.insertInto(MEDIA)
            .set(MEDIA.FILENAME, filename)
            .set(MEDIA.AUTHORID, authorId)
            .set(MEDIA.MEDIA_TYPE, mediaType.asDbMediaType())
            .set(MEDIA.BIRTH_DATE, birthDate)
            .set(MEDIA.CONTENT, content)
            .set(MEDIA.IS_TEMPORARY, isTemporary)
            .returningResult()
            .fetchOne()
            ?.toMedia()
    }

    override fun makeTemporary(filename: String): MediaFile? =
        updateTemporaryMark(
            filename = filename,
            isTemporary = true,
        )

    override fun makeRegular(filename: String): MediaFile? =
        updateTemporaryMark(
            filename = filename,
            isTemporary = false,
        )

    private fun updateTemporaryMark(
        filename: String,
        isTemporary: Boolean,
    ): MediaFile? =
        jooqContext.update(MEDIA)
            .set(MEDIA.IS_TEMPORARY, isTemporary)
            .where(MEDIA.FILENAME.eq(filename))
            .returningResult()
            .fetchOne()
            ?.toMedia()

    override fun deleteMedia(filename: String) {
        jooqContext.deleteFrom(MEDIA)
            .where(MEDIA.FILENAME.eq(filename))
            .execute()
    }

    private fun deleteAllOldTempMedia(elderThanDays: Long = REMOVING_TEMP_FILES_LIMIT_IN_DAYS) {
        jooqContext.deleteFrom(MEDIA)
            .where(
                MEDIA.IS_TEMPORARY.eq(true)
                    .and(MEDIA.BIRTH_DATE.lessOrEqual(LocalDateTime.now().minusDays(elderThanDays)))
            ).execute()
    }

    override fun selectMediaWithMeta(filename: String): MediaFile? =
        jooqContext.select(
            MEDIA.FILENAME,
            MEDIA.AUTHORID,
            MEDIA.MEDIA_TYPE,
            MEDIA.BIRTH_DATE,
            MEDIA.CONTENT,
            MEDIA.IS_TEMPORARY,
        ).from(MEDIA)
            .where(MEDIA.FILENAME.eq(filename))
            .fetchOne()
            ?.toMedia()

    override fun selectOnlyMedia(filename: String): ByteArray? =
        jooqContext.select(MEDIA.CONTENT).from(MEDIA)
            .where(MEDIA.FILENAME.eq(filename))
            .fetchOne()
            ?.toContentStream()

    override fun selectOnlyMeta(filename: String): MediaFile? =
        jooqContext.select(
            MEDIA.FILENAME,
            MEDIA.AUTHORID,
            MEDIA.MEDIA_TYPE,
            MEDIA.BIRTH_DATE,
            MEDIA.IS_TEMPORARY,
        ).from(MEDIA)
            .where(MEDIA.FILENAME.eq(filename))
            .fetchOne()
            ?.toMediaMeta()

    override fun selectMediaByUserIDAndMediaType(
        userID: Int,
        mediaType: MediaType,
    ): List<MediaFile> =
        jooqContext.select(
            MEDIA.FILENAME,
            MEDIA.AUTHORID,
            MEDIA.MEDIA_TYPE,
            MEDIA.BIRTH_DATE,
            MEDIA.CONTENT,
            MEDIA.IS_TEMPORARY,
        ).from(MEDIA)
            .where(MEDIA.AUTHORID.eq(userID).and(MEDIA.MEDIA_TYPE.eq(mediaType.asDbMediaType())))
            .fetch()
            .mapNotNull {
                it.toMedia()
            }
}

private fun Record.toMediaMeta(): MediaFile? =
    safeLet(
        this[MEDIA.FILENAME],
        this[MEDIA.AUTHORID],
        this[MEDIA.BIRTH_DATE],
        this[MEDIA.MEDIA_TYPE],
        this[MEDIA.IS_TEMPORARY],
    ) { fileName, authorId, birthDate, mediaType, isTemporary ->
        MediaFile(
            mediaType = mediaType.asAppMediaType(),
            filename = fileName,
            authorId = authorId,
            birthDate = birthDate,
            isTemporary = isTemporary,
            content = byteArrayOf(),
        )
    }

private fun Record.toMedia(): MediaFile? =
    safeLet(
        this[MEDIA.FILENAME],
        this[MEDIA.AUTHORID],
        this[MEDIA.BIRTH_DATE],
        this[MEDIA.MEDIA_TYPE],
        this[MEDIA.CONTENT],
        this[MEDIA.IS_TEMPORARY],
    ) { fileName, authorId, birthDate, mediaType, content, isTemporary ->
        MediaFile(
            mediaType = mediaType.asAppMediaType(),
            filename = fileName,
            authorId = authorId,
            birthDate = birthDate,
            isTemporary = isTemporary,
            content = content,
        )
    }

private fun Record.toContentStream(): ByteArray? = this[MEDIA.CONTENT]

private fun MediaType.asDbMediaType(): ContentType =
    when (this) {
        MediaType.IMAGE -> ContentType.IMAGE
        MediaType.VIDEO -> ContentType.VIDEO
        MediaType.SOUND -> ContentType.SOUND
    }

private fun ContentType.asAppMediaType(): MediaType =
    when (this) {
        ContentType.IMAGE -> MediaType.IMAGE
        ContentType.VIDEO -> MediaType.VIDEO
        ContentType.SOUND -> MediaType.SOUND
    }
