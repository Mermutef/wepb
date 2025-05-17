package ru.yarsu.domain.operations.directions

import dev.forkhandles.result4k.Result4k
import ru.yarsu.domain.dependencies.DirectionsDatabase
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.User

class DirectionOperationsHolder (
    private val directionsDatabase: DirectionsDatabase
) {
    val createDirection: (
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int,
        ) -> Result4k<Direction, DirectionCreationError> =
            CreateDirection(
                insertDirection = { name,
                                    description,
                                    logoPath,
                                    bannerPath,
                                    chairmanId,
                                    deputyChairmanId ->
                directionsDatabase.insertDirection(
                    name = name,
                    description = description,
                    logoPath = logoPath,
                    bannerPath = bannerPath,
                    chairmanId = chairmanId,
                    deputyChairmanId = deputyChairmanId
                )
            })

    val removeDirection: RemoveDirection =
        RemoveDirection(directionsDatabase::deleteDirection)

    val updateDirection: (
        Direction,
        String,
        String,
        String,
        String,
        Int,
        Int,
        ) -> Result4k<Direction, DirectionUpdateError> =
        UpdateDirection(
            updateDirection = directionsDatabase::updateDirection
        )

    val changeChairman: (Direction,Int) -> Result4k<Direction, ChairmanChangingError> =
        ChangeChairman(
            changeChairman = directionsDatabase::updateChairman
        )

    val changeDeputyChairman: (Direction, Int) -> Result4k<Direction, DeputyChairmanChangingError> =
        ChangeDeputyChairman(
            changeDeputyChairman = directionsDatabase::updateDeputyChairman
        )

    val fetchDirectionByID: (Int) -> Result4k<Direction, DirectionFetchingError> =
        FetchDirectionByID { directionId: Int ->
            directionsDatabase.selectDirectionByID(directionId)
        }

    val fetchChairmanDirectionID: (Int) -> Result4k<User, DirectionFetchingError> =
        FetchChairmanDirectionID { directionId: Int ->
            directionsDatabase.selectChairmanByDirectionID(directionId)
        }
    val fetchDeputyChairmanByDirectionID: (Int) ->Result4k<User, DirectionFetchingError> =
        FetchDeputyChairmanByDirectionID { directionId: Int ->
            directionsDatabase.selectDeputyChairmanByDirectionID(directionId)
        }

    val fetchDirectionNameAndLogoByID: (Int) -> Result4k<Direction, DirectionFetchingError> =
        FetchDirectionNameAndLogoByID { directionId: Int ->
            directionsDatabase.selectDirectionNameAndLogoByID(directionId)
        }

    val fetchAllDirections: () -> Result4k<List<Direction>, DirectionFetchingError> =
        FetchAllDirections {
            directionsDatabase.selectAllDirections()
        }

    val fetchAllDirectionsNameAndLogo: () -> Result4k<List<Direction>, DirectionFetchingError> =
        FetchAllDirections {
            directionsDatabase.selectAllDirectionsNameAndLogo()
        }
}
