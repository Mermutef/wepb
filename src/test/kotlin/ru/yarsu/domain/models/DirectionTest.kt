package ru.yarsu.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.yarsu.db.validDirectionDescription
import ru.yarsu.db.validDirectionName
import ru.yarsu.db.validFileName

class DirectionTest : FunSpec({

    test("blank name") {
        Direction.validateName(
            "  \t\n"
        ) shouldBe DirectionValidationResult.NAME_IS_BLANK_OR_EMPTY
    }

    test("too long name") {
        Direction.validateName(
            "a".repeat(Direction.MAX_NAME_LENGTH + 1)
        ) shouldBe DirectionValidationResult.NAME_IS_TOO_LONG
    }

    test("not matching pattern name") {
        Direction.validateName(
            "Sport Napravlenie"
        ) shouldBe DirectionValidationResult.NAME_PATTERN_MISMATCH
    }

    test("valid name") {
        Direction.validateName(
            validDirectionName
        ) shouldBe null
    }

    test("blank description") {
        Direction.validateDescription(
            "  \t\n"
        ) shouldBe DirectionValidationResult.DESCRIPTION_IS_BLANK_OR_EMPTY
    }

    test("not matching pattern description") {
        Direction.validateDescription(
            "@@@"
        ) shouldBe DirectionValidationResult.DESCRIPTION_PATTERN_MISMATCH
    }

    test("valid description") {
        Direction.validateDescription(
            validDirectionDescription
        ) shouldBe null
    }

    test("blank path") {
        Direction.validatePath(
            "  \t\n"
        ) shouldBe DirectionValidationResult.PATH_IS_BLANK_OR_EMPTY
    }

    test("valid path") {
        Direction.validatePath(
            validFileName
        ) shouldBe null
    }

    test("invalid ID") {
        Direction.validateID(
            -1
        ) shouldBe DirectionValidationResult.INCORRECT_ID
    }

    test("valid ID") {
        Direction.validateID(
            0
        ) shouldBe null
    }

    test("valid direction") {
        val direction = Direction(
            1,
            validDirectionName,
            validDirectionDescription,
            validFileName,
            validFileName,
            0,
            0,
        )
        direction.shouldNotBeNull()

        Direction.validateDirectionData(
            validDirectionName,
            validDirectionDescription,
            validFileName,
            validFileName,
            0,
            0,
        ) shouldBe DirectionValidationResult.ALL_OK
    }
})
