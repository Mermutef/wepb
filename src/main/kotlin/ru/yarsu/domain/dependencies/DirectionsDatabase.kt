package ru.yarsu.domain.dependencies

import ru.yarsu.domain.models.Direction

@Suppress("detekt:TooManyFunctions")
interface DirectionsDatabase {

    @Suppress("detekt:LongParameterList")
    fun insertDirection(
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int,
    ): Direction?

    @Suppress("detekt:LongParameterList")
    fun updateDirection(
        directionID: Int,
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int,
    ): Direction?

    fun deleteDirection(directionID: Int)

    fun selectDirectionByID(directionID: Int): Direction?

    fun selectDirectionByName(directionName: String): Direction?

    fun selectChairmanIDByDirectionID(directionID: Int): Int?

    fun selectDeputyChairmanIDByDirectionID(directionID: Int): Int?

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
