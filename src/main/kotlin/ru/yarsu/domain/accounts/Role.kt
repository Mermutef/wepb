package ru.yarsu.domain.accounts

@Suppress("detekt:LongParameterList")
enum class Role(
    val writeEditDeleteComment: Boolean = false, // Оставить, изменять, удалять комментарий
    val viewInformationBureau: Boolean = false, // Просматривать публичную информацию о профбюро
    val manageYourPost: Boolean = false, // управлять своими постами
    val manageMediaFile: Boolean = false, // Загружать, просматривать, прикреплять медиафайлы
    val manageAllPosts: Boolean = false, // редактировать, публиковать, скрывать посты (свои и писателей)
    val hideCommentOtherUser: Boolean = false, // Скрывать комментарии под постами
    val viewListUser: Boolean = false, // Просматривать список пользователей в системе, их роли
    val updateRole: Boolean = false, // выдавать и снимать полномочия модераторов и писателей
) {
    ANONYMOUS(),
    READER(
        writeEditDeleteComment = true,
        viewInformationBureau = true
    ),
    WRITER(
        writeEditDeleteComment = true,
        viewInformationBureau = true,
        manageYourPost = true,
        manageMediaFile = true,
    ),
    MODERATOR(
        writeEditDeleteComment = true,
        viewInformationBureau = true,
        manageYourPost = true,
        manageMediaFile = true,
        hideCommentOtherUser = true,
        manageAllPosts = true,
    ),
    ADMIN(
        writeEditDeleteComment = true,
        viewInformationBureau = true,
        manageYourPost = true,
        manageMediaFile = true,
        hideCommentOtherUser = true,
        manageAllPosts = true,
        viewListUser = true,
        updateRole = true
    ),
}
