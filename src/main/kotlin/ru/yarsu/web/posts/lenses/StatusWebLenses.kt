package ru.yarsu.web.posts.lenses

import org.http4k.lens.BiDiMapping
import org.http4k.lens.FormField
import org.http4k.lens.map
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import ru.yarsu.domain.models.Status
import ru.yarsu.web.lenses.GeneralWebLenses.makeBodyLensForFields

object StatusWebLenses {
    private fun String.toStatusEnum(): Status = Status.valueOf(this)

    val statusField = FormField
        .nonEmptyString()
        .nonBlankString()
        .map(
            BiDiMapping(
                asOut = { status: String ->
                    status.takeIf {
                        it == "PUBLISHED" || it == "HIDDEN" || it == "MODERATION" || it == "DRAFT"
                    }
                        .let { it?.toStatusEnum() ?: throw IllegalArgumentException("") }
                },
                asIn = { value: Status -> value.toString() }
            ),
        ).required("selectStatus")

    val formStatusField = makeBodyLensForFields(
        statusField
    )
}
