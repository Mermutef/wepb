package ru.yarsu.domain.operations.media

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.DirectionValidationResult
import ru.yarsu.domain.models.User

class CreateDirection(
    val insertDirection: (
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairman_id: Int,
        deputy_сhairman_id: Int
    ) -> Direction?,
    private val fetchDirectionByName: (String) -> Direction?,
    private val fetchDirectionByСhairman: (Int) -> Direction?,
) : ( String, String, String, String, Int, Int ) -> Result<Direction, DirectionCreationError> {

    override fun invoke(
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairman_id: Int,
        deputy_сhairman_id: Int
    ): Result<Direction, DirectionCreationError> =
        when {
            Direction.validateDirectionData(name, description, logoPath, bannerPath, chairman_id, deputy_сhairman_id)
                    != DirectionValidationResult.ALL_OK ->
                        Failure(DirectionCreationError.INVALID_DIRECTION_DATA)
            nameAlreadyExists(name) ->
                Failure(DirectionCreationError.NAME_ALREADY_EXISTS)
            chairmanAlreadyExists(chairman_id) ->
                Failure(DirectionCreationError.CHAIRMAN_ALREADY_EXISTS)
            deputyСhairmanAlreadyExists(deputy_сhairman_id) ->
                Failure(DirectionCreationError.DEPUTY_CHAIRMAN_ALREADY_EXISTS)
            else ->
                when (
                    val newDirection = insertDirection(
                        name,
                        description,
                        logoPath,
                        bannerPath,
                        chairman_id,
                        deputy_сhairman_id
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

    private fun chairmanAlreadyExists(chairman_id: Int): Boolean =
        when (fetchDirectionByСhairman(chairman_id)) {
            is Direction -> true
            else -> false
        }

    private fun deputyСhairmanAlreadyExists(deputy_сhairman_id: Int): Boolean =
        when (fetchDirectionByСhairman(deputy_сhairman_id)) {
            is Direction -> true
            else -> false
        }

}

enum class DirectionCreationError {
    NAME_ALREADY_EXISTS,
    CHAIRMAN_ALREADY_EXISTS,
    DEPUTY_CHAIRMAN_ALREADY_EXISTS,
    INVALID_DIRECTION_DATA,
    UNKNOWN_DATABASE_ERROR
}
