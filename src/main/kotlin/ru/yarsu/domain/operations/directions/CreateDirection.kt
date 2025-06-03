package ru.yarsu.domain.operations.directions

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.DirectionValidationResult

class CreateDirection(
    val insertDirection: (
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int,
    ) -> Direction?,
    private val fetchDirectionByName: (String) -> Direction?,
) : (String, String, String, String, Int, Int) -> Result<Direction, DirectionCreationError> {

    override fun invoke(
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int,
    ): Result<Direction, DirectionCreationError> =
        when {
            Direction.validateDirectionData(
                name,
                description,
                logoPath,
                bannerPath,
                chairmanId,
                deputyChairmanId,
            ) != DirectionValidationResult.ALL_OK ->
                Failure(DirectionCreationError.INVALID_DIRECTION_DATA)
            nameAlreadyExists(name) ->
                Failure(DirectionCreationError.NAME_ALREADY_EXISTS)
            else ->
                when (
                    val newDirection = insertDirection(
                        name,
                        description,
                        logoPath,
                        bannerPath,
                        chairmanId,
                        deputyChairmanId
                    )
                ) {
                    is Direction -> Success(newDirection)
                    else -> Failure(DirectionCreationError.UNKNOWN_DATABASE_ERROR)
                }
        }

    private fun nameAlreadyExists(name: String): Boolean =
        when (fetchDirectionByName(name)) {
            is Direction -> true
            else -> false
        }
}

enum class DirectionCreationError {
    INVALID_DIRECTION_DATA,
    UNKNOWN_DATABASE_ERROR,
    NAME_ALREADY_EXISTS,
}
