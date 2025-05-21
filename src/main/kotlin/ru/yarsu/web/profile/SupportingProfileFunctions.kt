package ru.yarsu.web.profile

internal fun String.crop(
    size: Int
): String {
    return when {
        this.length < size -> this
        else -> "${this.slice(0..size)}..."
    }
}