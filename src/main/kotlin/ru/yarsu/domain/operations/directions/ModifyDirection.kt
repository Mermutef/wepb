package ru.yarsu.domain.operations.directions

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.DirectionValidationResult

class RemoveDirection(
    val removeDirection: (Int) -> Unit,
) : (Direction) -> Result4k<Boolean, DirectionRemovingError> {
    override fun invoke(direction: Direction): Result4k<Boolean, DirectionRemovingError> =
        try {
            removeDirection(direction.id)
            Success(true)
        } catch (_: DataAccessException) {
            Failure(DirectionRemovingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class DirectionRemovingError {
    UNKNOWN_DATABASE_ERROR,
}

class UpdateDirection(
    val updateDirection: (
        directionId: Int,
        newName: String,
        newDescription: String,
        newLogoPath: String,
        newBannerPath: String,
        newChairmanId: Int,
        newDeputyChairmanId: Int,
    ) -> Direction?,
) : (
        Direction,
        String,
        String,
        String,
        String,
        Int,
        Int,
    ) -> Result4k<Direction, DirectionUpdateError> {
    override fun invoke(
        direction: Direction,
        newName: String,
        newDescription: String,
        newLogoPath: String,
        newBannerPath: String,
        newChairmanId: Int,
        newDeputyChairmanId: Int,
    ): Result4k<Direction, DirectionUpdateError> =
        try {
            when {
                Direction.validateDirectionData(
                    newName,
                    newDescription,
                    newLogoPath,
                    newBannerPath,
                    newChairmanId,
                    newDeputyChairmanId
                ) != DirectionValidationResult.ALL_OK ->
                    Failure(DirectionUpdateError.INVALID_DIRECTION_DATA)
                else ->
                    when (
                        val newDirection = updateDirection(
                            direction.id,
                            newName,
                            newDescription,
                            newLogoPath,
                            newBannerPath,
                            newChairmanId,
                            newDeputyChairmanId
                        )
                    ) {
                        is Direction -> Success(newDirection)

                        else -> Failure(DirectionUpdateError.UNKNOWN_UPDATING_ERROR)
                    }
            }
        } catch (_: DataAccessException) {
            Failure(DirectionUpdateError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class DirectionUpdateError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_UPDATING_ERROR,
    INVALID_DIRECTION_DATA,
}

class ChangeChairman(
    val changeChairman: (directionId: Int, newChairmanId: Int) -> Direction?,
) : (Direction, Int) -> Result4k<Direction, ChairmanChangingError> {
    override fun invoke(
        direction: Direction,
        newChairmanId: Int,
    ): Result4k<Direction, ChairmanChangingError> =
        try {
            when {
                newChairmanId < 0 ->
                    Failure(ChairmanChangingError.INCORRECT_ID)
                else -> when (val directionWithNewChairman = changeChairman(direction.id, newChairmanId)) {
                    is Direction -> Success(directionWithNewChairman)

                    else -> Failure(ChairmanChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(ChairmanChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class ChairmanChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    INCORRECT_ID,
}

class ChangeDeputyChairman(
    val changeDeputyChairman: (directionId: Int, newDeputyChairmanId: Int) -> Direction?,
) : (Direction, Int) -> Result4k<Direction, DeputyChairmanChangingError> {
    override fun invoke(
        direction: Direction,
        newDeputyChairmanId: Int,
    ): Result4k<Direction, DeputyChairmanChangingError> =
        try {
            when {
                newDeputyChairmanId < 0 ->
                    Failure(DeputyChairmanChangingError.INCORRECT_ID)
                else -> when (val directionWithNewChairman = changeDeputyChairman(direction.id, newDeputyChairmanId)) {
                    is Direction -> Success(directionWithNewChairman)

                    else -> Failure(DeputyChairmanChangingError.UNKNOWN_CHANGING_ERROR)
                }
            }
        } catch (_: DataAccessException) {
            Failure(DeputyChairmanChangingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class DeputyChairmanChangingError {
    UNKNOWN_DATABASE_ERROR,
    UNKNOWN_CHANGING_ERROR,
    INCORRECT_ID,
}
