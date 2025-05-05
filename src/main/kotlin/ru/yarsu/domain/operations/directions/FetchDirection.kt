package ru.yarsu.domain.operations.directions

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.models.Direction

class FetchDirectionByID (
    private val selectDirectionByID: (Int) -> Direction?,
) : (Int) -> Result4k<Direction, DirectionFetchingError> {

    override operator fun invoke(directionId: Int): Result4k<Direction, DirectionFetchingError> =
        try {
            when (val direction = selectDirectionByID(directionId)) {
                is Direction -> Success(direction)
                else -> Failure(DirectionFetchingError.NO_SUCH_DIRECTION)
            }
        } catch (_: DataAccessException) {
            Failure(DirectionFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchChairmanIDByDirectionID (
    private val selectChairmanIDByDirectionByID: (Int) -> Int?,
) : (Int) -> Result4k<Int, DirectionFetchingError> {

    override operator fun invoke(directionId: Int): Result4k<Int, DirectionFetchingError> =
        try {
            when (val chairmanId = selectChairmanIDByDirectionByID(directionId)) {
                is Int -> Success(chairmanId)
                else -> Failure(DirectionFetchingError.NO_SUCH_ID)
            }
        } catch (_: DataAccessException) {
            Failure(DirectionFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchDeputyChairmanIDByDirectionID (
    private val selectDeputyChairmanIDByDirectionID: (Int) -> Int?,
) : (Int) -> Result4k<Int, DirectionFetchingError> {

    override operator fun invoke(directionId: Int): Result4k<Int, DirectionFetchingError> =
        try {
            when (val deputyChairmanId = selectDeputyChairmanIDByDirectionID(directionId)) {
                is Int -> Success(deputyChairmanId)
                else -> Failure(DirectionFetchingError.NO_SUCH_ID)
            }
        } catch (_: DataAccessException) {
            Failure(DirectionFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchDirectionNameAndLogoByID (
    private val selectDirectionNameAndLogoByID: (Int) -> Direction?,
) : (Int) -> Result4k<Direction, DirectionFetchingError> {

    override operator fun invoke(directionId: Int): Result4k<Direction, DirectionFetchingError> =
        try {
            when (val direction = selectDirectionNameAndLogoByID(directionId)) {
                is Direction -> Success(direction)
                else -> Failure(DirectionFetchingError.NO_SUCH_DIRECTION)
            }
        } catch (_: DataAccessException) {
            Failure(DirectionFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchAllDirections (
    private val selectAllDirections: () -> List<Direction>,
) : () -> Result4k<List<Direction>, DirectionFetchingError> {

    override operator fun invoke(): Result4k<List<Direction>, DirectionFetchingError> =
        try {
            Success(selectAllDirections())
        } catch (_: DataAccessException) {
            Failure(DirectionFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

class FetchAllDirectionsNameAndLogo (
    private val selectAllDirectionsNameAndLogo: () -> List<Direction>,
) : () -> Result4k<List<Direction>, DirectionFetchingError> {

    override operator fun invoke(): Result4k<List<Direction>, DirectionFetchingError> =
        try {
            Success(selectAllDirectionsNameAndLogo())
        } catch (_: DataAccessException) {
            Failure(DirectionFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}

enum class DirectionFetchingError {
    UNKNOWN_DATABASE_ERROR,
    NO_SUCH_DIRECTION,
    NO_SUCH_ID,
}
