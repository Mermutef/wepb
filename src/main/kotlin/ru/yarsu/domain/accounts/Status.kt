package ru.yarsu.domain.accounts

@Suppress("detekt:EmptyDefaultConstructor")
enum class Status() {
    PUBLISHED,
    HIDDEN,
    MODERATION,
    DRAFT,
}
