package ru.yarsu.domain.operations.users

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.jooq.exception.DataAccessException
import ru.yarsu.domain.accounts.Role
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.User

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

class FetchDirectionByName (
    private val selectDirectionByName: (String) -> Direction?,
) : (String) -> Result4k<Direction, DirectionFetchingError> {

    override operator fun invoke(directionName: String): Result4k<Direction, DirectionFetchingError> =
        try {
            selectDirectionByName(directionName)
                .let { direction ->
                    when (direction) {
                        is Direction -> Success(direction)
                        else -> Failure(DirectionFetchingError.NO_SUCH_DIRECTION)
                    }
                }
        } catch (_: DataAccessException) {
            Failure(DirectionFetchingError.UNKNOWN_DATABASE_ERROR)
        }
}





enum class DirectionFetchingError {
    UNKNOWN_DATABASE_ERROR,
    NO_SUCH_DIRECTION,
}
