package ru.yarsu.domain.accounts

enum class Role(
    val leaveComment: Boolean = false, // Комментировать посты
    val manageComment: Boolean = false, // Скрывать и делать видимыми комментарии других пользователей
    val writePosts: Boolean = false, // Писать посты
    val manageMediaFile: Boolean = false, // Загружать, просматривать, прикреплять медиафайлы
    val managePosts: Boolean = false, // Управлять постами
    val manageUsers: Boolean = false, // Управлять пользователями
) {
    ANONYMOUS,
    READER(leaveComment = true),
    WRITER(
        leaveComment = true,
        writePosts = true,
        manageMediaFile = true,
    ),
    MODERATOR(
        leaveComment = true,
        manageComment = true,
        writePosts = true,
        manageMediaFile = true,
        managePosts = true,
    ),
    ADMIN(manageUsers = true),
}
