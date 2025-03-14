package ru.yarsu.web.form

import org.http4k.lens.WebForm

data class CustomWebForm(
    val fields: Map<String, List<String>> = emptyMap(),
    val errors: Map<String, CustomFailure> = emptyMap(),
)

fun WebForm.toCustomForm(): CustomWebForm =
    CustomWebForm(
        fields = this.fields,
        errors = this.errors.associate {
            it.meta.name to CustomFailure(
                name = it.meta.name,
                description = it.meta.description
            )
        }
    )

fun CustomWebForm.addFailure(
    name: String,
    description: String?,
): CustomWebForm {
    val mutableErrors = this.errors.toMutableMap()
    mutableErrors[name] = CustomFailure(name = name, description = description)
    return this.copy(errors = mutableErrors.toMap())
}

fun emptyCustomForm(): CustomWebForm = WebForm(emptyMap(), emptyList()).toCustomForm()
