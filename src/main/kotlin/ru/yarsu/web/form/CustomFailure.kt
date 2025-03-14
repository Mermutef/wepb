package ru.yarsu.web.form

import org.http4k.lens.Failure

data class CustomFailure(
    val name: String,
    val description: String? = null,
)

fun Failure.toCustomFailure(): CustomFailure =
    CustomFailure(
        name = this.meta.name,
        description = this.meta.description,
    )

fun List<Failure>.toListCustomFailure(): List<CustomFailure> = this.map { it.toCustomFailure() }
