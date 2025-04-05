package ru.yarsu.domain.accounts

enum class Role(
    val leaveComment: Boolean = false, // Оставить, изменять, удалять комментарий
    val manageComment: Boolean = false, // Оставить, изменять, удалять комментарий
    val writePosts: Boolean = false, // управлять своими постами
    val manageMediaFile: Boolean = false, // Загружать, просматривать, прикреплять медиафайлы
    val managePosts: Boolean = false, // редактировать, публиковать, скрывать посты (свои и писателей)
    val manageUsers: Boolean = false, // Просматривать список пользователей в системе, их роли
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
