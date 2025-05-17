package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.User

@Suppress("detekt:TooManyFunctions")
interface DirectionsDatabase {

    @Suppress("detekt:LongParameterList")
    fun insertDirection(
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int
    ): Direction?

    fun updateDirection(
        directionID: Int,
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int
    ): Direction?

    fun deleteDirection(directionID: Int)

    fun selectDirectionByID(directionID: Int): Direction?

    fun selectChairmanByDirectionID(directionID: Int): User?

    fun selectDeputyChairmanByDirectionID(directionID: Int): User?

    fun selectDirectionNameAndLogoByID(directionID: Int): Direction?

    fun selectAllDirections(): List<Direction>

    fun selectAllDirectionsNameAndLogo(): List<Direction>

    fun updateChairman(
        directionID: Int,
        chairmanId: Int,
    ): Direction?

    fun updateDeputyChairman(
        directionID: Int,
        deputyChairmanId: Int,
    ): Direction?

}
