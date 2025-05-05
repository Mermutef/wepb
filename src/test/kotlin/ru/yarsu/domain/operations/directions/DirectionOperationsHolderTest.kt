package ru.yarsu.domain.operations.directions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.mockito.Mockito.mock
import ru.yarsu.db.directions.DirectionOperations

class DirectionOperationsHolderTest : FunSpec({
    val context: DSLContext = mock()

    val directionOperations = DirectionOperations(context)
    val directionOperationsHolder = DirectionOperationsHolder(directionOperations)

    test("DirectionOperationsHolder should initialize with provided direction operations") {
        directionOperationsHolder.createDirection::class
            .shouldBe(CreateDirection::class)
        directionOperationsHolder.fetchAllDirections::class
            .shouldBe(FetchAllDirections::class)
        directionOperationsHolder.fetchAllDirectionsNameAndLogo::class
            .shouldBe(FetchAllDirectionsNameAndLogo::class)
        directionOperationsHolder.fetchDirectionByID::class
            .shouldBe(FetchDirectionByID::class)
        directionOperationsHolder.fetchDirectionNameAndLogoByID::class
            .shouldBe(FetchDirectionNameAndLogoByID::class)
        directionOperationsHolder.fetchChairmanIDByDirectionID::class
            .shouldBe(FetchChairmanIDByDirectionID::class)
        directionOperationsHolder.fetchDeputyChairmanIDByDirectionID::class
            .shouldBe(FetchDeputyChairmanIDByDirectionID::class)
        directionOperationsHolder.updateDirection::class
            .shouldBe(UpdateDirection::class)
        directionOperationsHolder.removeDirection::class
            .shouldBe(RemoveDirection::class)
        directionOperationsHolder.changeChairman::class
            .shouldBe(ChangeChairman::class)
        directionOperationsHolder.changeDeputyChairman::class
            .shouldBe(ChangeDeputyChairman::class)
    }
})
