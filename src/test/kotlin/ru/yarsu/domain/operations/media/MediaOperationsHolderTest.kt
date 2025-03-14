package ru.yarsu.domain.operations.media

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.mockito.kotlin.mock
import ru.yarsu.db.media.MediaOperations

class MediaOperationsHolderTest : FunSpec({
    val context: DSLContext = mock()

    val mediaOperations = MediaOperations(context)
    val mediaOperationsHolder = MediaOperationsHolder(mediaOperations)

    test("MediaOperationsHolder should initialize with provided media operations") {
        mediaOperationsHolder.createMedia::class shouldBe CreateMedia::class
        mediaOperationsHolder.fetchOnlyMedia::class shouldBe FetchOnlyMedia::class
        mediaOperationsHolder.fetchMediaWithMeta::class shouldBe FetchMediaWithMeta::class
        mediaOperationsHolder.fetchOnlyMeta::class shouldBe FetchOnlyMeta::class
        mediaOperationsHolder.removeMedia::class shouldBe RemoveMedia::class
        mediaOperationsHolder.makeMediaRegular::class shouldBe MakeMediaRegular::class
        mediaOperationsHolder.makeMediaTemporary::class shouldBe MakeMediaTemporary::class
    }
})
