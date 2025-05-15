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

fun CustomWebForm.replaceOrAddFailuresFromForm(form: CustomWebForm): CustomWebForm {
    var newForm = this
    form.errors.values.forEach { failure ->
        newForm = newForm.addFailure(
            name = failure.name,
            description = failure.description,
        )
    }
    return newForm
}

fun CustomWebForm.replaceOrAddFieldsFromForm(form: CustomWebForm): CustomWebForm {
    var newForm = this
    form.fields.forEach { field ->
        newForm = newForm.replaceOrAddFieldValues(
            fieldName = field.key,
            values = field.value,
        )
    }
    return newForm
}

infix fun CustomWebForm.replaceOrAddFrom(form: CustomWebForm): CustomWebForm =
    replaceOrAddFieldsFromForm(form)
        .replaceOrAddFailuresFromForm(form)

fun CustomWebForm.replaceOrAddFieldValue(
    fieldName: String,
    value: String,
): CustomWebForm {
    return this.replaceOrAddFieldValues(fieldName, listOf(value))
}

fun CustomWebForm.replaceOrAddFieldValues(
    fieldName: String,
    values: List<String>,
): CustomWebForm {
    val mutableFields = this.fields.toMutableMap()
    mutableFields[fieldName] = values
    return this.copy(fields = mutableFields.toMap())
}

fun emptyCustomForm(): CustomWebForm = WebForm(emptyMap(), emptyList()).toCustomForm()
