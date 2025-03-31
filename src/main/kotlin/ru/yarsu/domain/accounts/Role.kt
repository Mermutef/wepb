package ru.yarsu.domain.accounts

enum class Role(
    val attachMediaFile: Boolean = false, // Прикреплять имеющиеся в приложении медиафайлы к постам
    val deleteYourPost: Boolean = false, // Удалять свои черновики постов
    val editAllPosts: Boolean = false, // Редактировать посты (свои и писателей)
    val editOrDeleteYourComment: Boolean = false, // Редактировать и удалить свой комментарий
    val editYourPost: Boolean = false, // Редактировать свои неопубликованные посты
    val filterData: Boolean = false, // Фильтровать по хештегам, дате публикации и последнего редактирования
    val hideCommentOtherUser: Boolean = false, // Скрывать комментарии под постами
    val hidePost: Boolean = false, // скрывать посты (свои и писателей)
    val publishNewPost: Boolean = false, // Публиковать новые посты
    val publishWritersPost: Boolean = false, // Публиковать посты писателей
    val recallPostForModeration: Boolean = false, // Отзывать посты с модерации
    val saveDraftPost: Boolean = false, // Сохранять черновики постов
    val sendPostForModeration: Boolean = false, // Отправлять посты на модерацию
    val viewAllMediaFile: Boolean = false, // Просматривать все имеющиеся в приложении медиафайлы
    val viewInformationBureau: Boolean = false, // Просматривать публичную информацию о профбюро
    val viewListUser: Boolean = false, // Просматривать список пользователей в системе, их роли
    val viewMedia: Boolean = false, // Просмотр постов, комментариев к постам, профилей модераторов
    val writeComment: Boolean = false, // Оставить комментарий
    val updateRole: Boolean = false, // выдавать и снимать полномочия модераторов и писателей
    val uploadMediaFile: Boolean = false, // Загружать медиафайлы (изображения, аудиофайлы, видео)
) {
    ANONYMOUS(
        viewMedia = true,
        filterData = true
    ),
    READER(
        viewMedia = true,
        filterData = true,
        writeComment = true,
        editOrDeleteYourComment = true,
        viewInformationBureau = true
    ),
    WRITER(
        viewMedia = true,
        filterData = true,
        writeComment = true,
        editOrDeleteYourComment = true,
        viewInformationBureau = true,
        saveDraftPost = true,
        sendPostForModeration = true,
        recallPostForModeration = true,
        editYourPost = true,
        deleteYourPost = true,
        uploadMediaFile = true,
        viewAllMediaFile = true,
        attachMediaFile = true
    ),
    MODERATOR(
        viewMedia = true,
        filterData = true,
        writeComment = true,
        editOrDeleteYourComment = true,
        viewInformationBureau = true,
        saveDraftPost = true,
//        ?
        sendPostForModeration = true,
        recallPostForModeration = true,
//        ?
        editYourPost = true,
        deleteYourPost = true,
        uploadMediaFile = true,
        viewAllMediaFile = true,
        attachMediaFile = true,
        hideCommentOtherUser = true,
        publishWritersPost = true,
        publishNewPost = true,
        editAllPosts = true,
        hidePost = true
    ),
    ADMIN(
        viewMedia = true,
        filterData = true,
        writeComment = true,
        editOrDeleteYourComment = true,
        viewInformationBureau = true,
        saveDraftPost = true,
//       ?
        sendPostForModeration = true,
        recallPostForModeration = true,
//       ?
        editYourPost = true,
        deleteYourPost = true,
        uploadMediaFile = true,
        viewAllMediaFile = true,
        attachMediaFile = true,
        hideCommentOtherUser = true,
        publishWritersPost = true,
        publishNewPost = true,
        editAllPosts = true,
        hidePost = true,
        viewListUser = true,
        updateRole = true
    ),
}
