document.addEventListener('DOMContentLoaded', () => {
    // Функции экранирования и форматирования
    const escapeHtml = (text) => {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    };

    const formatDate = (timestamp) => {
        // Конвертируем секунды в миллисекунды
        const date = new Date(timestamp * 1000);

        // Проверка на валидность даты
        if (isNaN(date.getTime())) {
            console.warn('Invalid timestamp:', timestamp);
            return 'Дата неизвестна';
        }

        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');

        return `${day}.${month}.${year} ${hours}:${minutes}`;
    };

    const formatContent = (content) => {
        return escapeHtml(content).replace(/\n/g, '<br>');
    };

    // Загрузка комментариев
    const loadComments = async () => {
        try {
            const commentHolder = document.getElementById('comment-holder');
            if (!commentHolder) return;

            const postId = commentHolder.dataset.postId;
            if (!postId) throw new Error('Post ID not found');

            const response = await fetch(`/comments/${postId}/all`);
            if (!response.ok) throw new Error('Ошибка загрузки комментариев');

            const comments = await response.json();
            console.log(comments);
            const fragment = document.createDocumentFragment();

            // Удаляем старые комментарии и сообщение об отсутствии
            document.querySelectorAll('.comment-item, .no-comments').forEach(el => el.remove());

            // Если комментариев нет - показываем сообщение
            if (comments.length === 0) {
                // ... код для no-comments без изменений ...
            }
            // Если есть комментарии - отображаем их
            else {
                comments.forEach(comment => {
                    // Точная проверка редактирования с учетом секунд
                    const isEdited = Math.abs(comment.comment.lastModifyDate - comment.comment.creationDate) > 1;

                    const commentElement = document.createElement('div');
                    commentElement.className = 'col comment-item';
                    commentElement.innerHTML = `
                        <div class="row mb-2">
                            <div class="col">
                                <i class="text-orange">@${escapeHtml(comment.author.login)}</i>
                            </div>
                            <div class="col text-end">
                                <i class="text-muted fs-6">
                                    ${formatDate(comment.comment.creationDate)}
                                    ${isEdited ? '<span class="text-orange"> (ред.)</span>' : ''}
                                </i>
                            </div>
                        </div>
                        <div class="row ms-3">
                            <div class="col">
                                ${formatContent(comment.comment.content)}
                            </div>
                        </div>
                        <hr>
                    `;
                    fragment.appendChild(commentElement);
                });
            }

            // Вставляем новые элементы перед comment-holder
            commentHolder.parentNode.appendChild(fragment);

        } catch (error) {
            console.error('Ошибка:', error);
        }
    };

    // Отправка нового комментария
    const setupCommentForm = () => {
        const form = document.getElementById('comment-form');
        const button = document.getElementById('comment-button');
        const textarea = document.getElementById('comment-area');

        if (!form || !button || !textarea) return;

        button.addEventListener('click', async () => {
            try {
                // Удаляем предыдущие ошибки
                const existingError = form.closest('.row').querySelector('.alert.alert-danger');
                if (existingError) existingError.remove();

                const content = textarea.value.trim();
                if (!content) {
                    showError('Комментарий не может быть пустым', form);
                    return;
                }

                const commentHolder = document.getElementById('comment-holder');
                const postId = commentHolder.dataset.postId;

                const response = await fetch(`/comments/${postId}/new`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({content})
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Ошибка при отправке комментария');
                }

                // Очищаем поле и обновляем комментарии
                textarea.value = '';
                await loadComments();

            } catch (error) {
                showError(error.message, form);
            }
        });
    };

    // Показ ошибок
    const showError = (message, formElement) => {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger mt-2 mb-0 text-center thin-input';
        errorDiv.setAttribute('role', 'alert');
        errorDiv.textContent = message;

        // Вставляем ошибку перед формой
        formElement.append(errorDiv);
    };

    // Инициализация
    const init = () => {
        loadComments();
        setupCommentForm();
    };

    init();
});