package ru.yarsu.db.directions

import org.jooq.DSLContext
import org.jooq.Record
import ru.yarsu.db.generated.tables.Directions.Companion.DIRECTIONS
import ru.yarsu.db.users.UserOperations
import ru.yarsu.db.utils.safeLet
import ru.yarsu.domain.dependencies.DirectionsDatabase
import ru.yarsu.domain.models.Direction
import ru.yarsu.domain.models.User

@Suppress("detekt:TooManyFunctions")
class DirectionOperations(
    private val jooqContext: DSLContext,
) : DirectionsDatabase {

    override fun insertDirection(
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int
    ): Direction? = jooqContext.insertInto(DIRECTIONS)
                    .set(DIRECTIONS.NAME, name)
                    .set(DIRECTIONS.DESCRIPTION, description)
                    .set(DIRECTIONS.LOGO_PATH, logoPath)
                    .set(DIRECTIONS.BANNER_PATH, bannerPath)
                    .set(DIRECTIONS.CHAIRMAN_ID, chairmanId)
                    .set(DIRECTIONS.DEPUTY_СHAIRMAN_ID, deputyChairmanId)
                    .returningResult()
                    .fetchOne()
                    ?.toDirection()

    override fun updateDirection(
        directionID: Int,
        name: String,
        description: String,
        logoPath: String,
        bannerPath: String,
        chairmanId: Int,
        deputyChairmanId: Int
    ): Direction? = jooqContext.update(DIRECTIONS)
        .set(DIRECTIONS.NAME, name)
        .set(DIRECTIONS.DESCRIPTION, description)
        .set(DIRECTIONS.LOGO_PATH, logoPath)
        .set(DIRECTIONS.BANNER_PATH, bannerPath)
        .set(DIRECTIONS.CHAIRMAN_ID, chairmanId)
        .set(DIRECTIONS.DEPUTY_СHAIRMAN_ID, deputyChairmanId)
        .where(DIRECTIONS.ID.eq(directionID))
        .returningResult()
        .fetchOne()
        ?.toDirection()

    override fun deleteDirection(directionID: Int){
        jooqContext.deleteFrom(DIRECTIONS)
            .where(DIRECTIONS.ID.eq(directionID))
            .execute()
    }

    override fun selectDirectionByID(directionID: Int): Direction? =
        selectFromDirections()
            .where(DIRECTIONS.ID.eq(directionID))
            .fetchOne()
            ?.toDirection()

    override fun selectChairmanByDirectionID(directionID: Int): User? {
        val result = selectFromDirections()
          .where(DIRECTIONS.ID.eq(directionID))
          .fetchOne()
          ?.toDirection()
        if (result == null)
            return result
        return UserOperations(jooqContext)
            .selectUserByID(
                result.chairmanId
            )
    }

    override fun selectDeputyChairmanByDirectionID(directionID: Int): User? {
        val result = selectFromDirections()
            .where(DIRECTIONS.ID.eq(directionID))
            .fetchOne()
            ?.toDirection()
        if (result == null)
            return result
        return UserOperations(jooqContext)
            .selectUserByID(
                result.deputyChairmanId
            )
    }

    override fun selectDirectionNameAndLogoByID(directionID: Int): Direction? =
        selectFromDirections()
            .where(DIRECTIONS.ID.eq(directionID))
            .fetchOne()
            ?.toDirectionNameAndLogo()

    override fun selectAllDirections(): List<Direction> =
        selectFromDirections()
            .fetch()
            .mapNotNull { it.toDirection() }

    override fun selectAllDirectionsNameAndLogo(): List<Direction> =
        selectFromDirections()
            .fetch()
            .mapNotNull { it.toDirectionNameAndLogo() }

    override fun updateChairman(
        directionID: Int,
        chairmanId: Int,
    ): Direction? =
        jooqContext.update(DIRECTIONS)
            .set(DIRECTIONS.CHAIRMAN_ID, chairmanId)
            .where(DIRECTIONS.ID.eq(directionID))
            .returningResult()
            .fetchOne()
            ?.toDirection()

    override fun updateDeputyChairman(
        directionID: Int,
        deputyChairmanId: Int,
    ): Direction? =
        jooqContext.update(DIRECTIONS)
            .set(DIRECTIONS.DEPUTY_СHAIRMAN_ID , deputyChairmanId)
            .where(DIRECTIONS.ID.eq(directionID))
            .returningResult()
            .fetchOne()
            ?.toDirection()

    private fun selectFromDirections() =
        jooqContext
            .select(
                DIRECTIONS.ID,
                DIRECTIONS.NAME,
                DIRECTIONS.DESCRIPTION,
                DIRECTIONS.LOGO_PATH,
                DIRECTIONS.BANNER_PATH,
                DIRECTIONS.CHAIRMAN_ID,
                DIRECTIONS.DEPUTY_СHAIRMAN_ID
            )
            .from(DIRECTIONS)

}


private fun Record.toDirection(): Direction? =
    safeLet(
        this[DIRECTIONS.ID],
        this[DIRECTIONS.NAME],
        this[DIRECTIONS.DESCRIPTION],
        this[DIRECTIONS.LOGO_PATH],
        this[DIRECTIONS.BANNER_PATH],
        this[DIRECTIONS.CHAIRMAN_ID],
        this[DIRECTIONS.DEPUTY_СHAIRMAN_ID],
    ) { id, name, description, logoPath, bannerPath, chairmanId, deputyChairmanId->
        Direction(
            id = id,
            name = name.trim(),
            description = description.trim(),
            logoPath = logoPath.trim(),
            bannerPath = bannerPath.trim(),
            chairmanId = chairmanId,
            deputyChairmanId = deputyChairmanId,
        )
    }

private fun Record.toDirectionNameAndLogo(): Direction? =
    safeLet(
        this[DIRECTIONS.ID],
        this[DIRECTIONS.NAME],
        this[DIRECTIONS.LOGO_PATH],
    ) { id, name, logoPath->
        Direction(
            id = id,
            name = name.trim(),
            description = "",
            logoPath = logoPath.trim(),
            bannerPath = "",
            chairmanId = -1,
            deputyChairmanId = -1,
        )
    }