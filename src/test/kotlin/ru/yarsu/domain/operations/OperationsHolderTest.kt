package ru.yarsu.domain.operations

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.yarsu.db.media.MediaOperations
import ru.yarsu.domain.dependencies.DatabaseOperations
import ru.yarsu.domain.dependencies.UsersDatabase
import ru.yarsu.domain.operations.media.MediaOperationsHolder
import ru.yarsu.domain.operations.users.UserOperationsHolder

class OperationsHolderTest : FunSpec({
    val mockUserOperations: UsersDatabase = mock()
    val mockDBOperations: DatabaseOperations = mock()
    val mockMediaOperations: MediaOperations = mock()

    whenever(mockDBOperations.userOperations).thenReturn(mockUserOperations)
    whenever(mockDBOperations.mediaOperations).thenReturn(mockMediaOperations)

    test("OperationsHolder should initialize with provided user and group operations") {
        val operations = OperationsHolder(mockDBOperations, config)
        operations.userOperations::class shouldBe UserOperationsHolder::class
        operations.mediaOperations::class shouldBe MediaOperationsHolder::class
    }
})
