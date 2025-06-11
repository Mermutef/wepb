document.addEventListener('DOMContentLoaded', () => {
    // Кэш элементов
    const elements = {
        commentHolder: document.getElementById('comment-holder'),
        commentForm: document.getElementById('comment-form'),
        commentButton: document.getElementById('comment-button'),
        commentTextarea: document.getElementById('comment-area')
    };

    // Вспомогательные функции
    const escapeHtml = text => text.replace(/[&<>"']/g, m => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;',
        '"': '&quot;', "'": '&#39;'
    }[m]));

    const formatDate = timestamp => {
        const date = new Date(timestamp * 1000);
        if (isNaN(date.getTime())) return 'Дата неизвестна';
        return `${date.getDate().toString().padStart(2, '0')}.${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getFullYear()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    };

    const formatContent = content => escapeHtml(content).replace(/\n/g, '<br>');

    // Состояние приложения
    const state = {
        currentEdit: null, // {id: number, element: HTMLElement, original: string}
    };

    // Функция отмены редактирования
    const cancelEdit = () => {
        if (!state.currentEdit) return;

        state.currentEdit.element.innerHTML = state.currentEdit.original;
        state.currentEdit = null;
    };

    // Функция начала редактирования (переработанная)
    const startEdit = async (commentId, element) => {
        // Закрываем предыдущее редактирование
        if (state.currentEdit) cancelEdit();

        // Сохраняем оригинальное содержимое
        state.currentEdit = {
            id: commentId,
            element: element,
            original: element.innerHTML
        };

        // Быстрая визуальная замена
        element.innerHTML = '<div class="text-center py-2">Загрузка...</div>';

        try {
            const response = await fetch(`/comments/${commentId}`);
            if (!response.ok) throw new Error();
            const data = await response.json();

            // Проверяем, не отменили ли мы редактирование во время загрузки
            if (!state.currentEdit || state.currentEdit.id !== commentId) return;

            element.innerHTML = `
                <div class="edit-comment-form">
                    <textarea class="form-control mb-2" rows="3" autofocus>${data.content}</textarea>
                    <div class="d-flex justify-content-end mt-2">
                        <button type="button" class="btn btn-secondary me-2 cancel-edit">Отмена</button>
                        <button type="button" class="btn btn-primary save-edit">Сохранить</button>
                    </div>
                </div>
                <hr>
            `;
        } catch {
            if (state.currentEdit?.id === commentId) {
                element.innerHTML = state.currentEdit.original;
                state.currentEdit = null;
            }
        }
    };

    // Функция сохранения комментария
    const saveEdit = async () => {
        if (!state.currentEdit) return;

        const element = state.currentEdit.element;
        const textarea = element.querySelector('textarea');
        if (!textarea) return;

        const content = textarea.value.trim();
        if (!content) return;

        // Визуализация процесса сохранения
        element.innerHTML = '<div class="text-center py-2">Сохранение...</div>';

        try {
            const response = await fetch(`/comments/${state.currentEdit.id}/edit`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({content})
            });

            if (!response.ok) throw new Error();

            // Обновляем только этот комментарий
            await loadComments();
        } catch {
            // При ошибке восстанавливаем форму
            element.innerHTML = `
                <div class="edit-comment-form">
                    <textarea class="form-control mb-2" rows="3">${content}</textarea>
                    <div class="d-flex justify-content-end mt-2">
                        <button type="button" class="btn btn-secondary me-2 cancel-edit">Отмена</button>
                        <button type="button" class="btn btn-primary save-edit">Сохранить</button>
                    </div>
                    <div class="text-danger mt-2">Ошибка сохранения</div>
                </div>
                <hr>
            `;
        }
    };

    // Функция удаления комментария
    const deleteComment = async commentId => {
        if (!confirm('Вы уверены, что хотите удалить комментарий? Это действие отменить невозможно')) return;

        const comment = document.getElementById(`comment-${commentId}`);
        if (comment) comment.style.opacity = '0.5';

        const response = await fetch(`/comments/${commentId}/delete`, {method: 'POST'});

        if (response.ok) {
            await loadComments();
        } else {
            if (comment) comment.style.opacity = '';
            showError('Не удалось удалить комментарий', elements.commentForm);
        }
    };

    // Загрузка комментариев (оптимизированная)
    const loadComments = async () => {
        if (!elements.commentHolder) return;
        const postId = elements.commentHolder.dataset.postId;
        if (!postId) return;

        // Отменяем активное редактирование
        if (state.currentEdit) cancelEdit();

        try {
            const response = await fetch(`/comments/${postId}/all`);
            if (!response.ok) throw new Error();
            renderComments(await response.json());
        } catch {
            showError('Ошибка загрузки комментариев', elements.commentHolder);
        }
    };

    // Отрисовка комментариев
    const renderComments = comments => {
        if (!elements.commentHolder) return;

        // Используем DocumentFragment для быстрой вставки
        const fragment = document.createDocumentFragment();
        const container = document.createElement('div');

        if (comments.length === 0) {
            container.innerHTML = `
                <div class="col text-center py-4">
                    <i class="text-muted">Еще никто не прокомментировал эту запись. Станьте первым!</i>
                </div>
                <hr>
            `;
        } else {
            comments.forEach(comment => {
                const isEdited = Math.abs(comment.comment.lastModifiedDate - comment.comment.creationDate) > 1;
                const isAuthor = userId && (comment.author.id == userId);

                container.innerHTML += `
                    <div class="col comment-item" id="comment-${comment.comment.id}">
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
                            <div class="col comment-content">
                                ${formatContent(comment.comment.content)}
                            </div>
                        </div>
                        ${(isAuthor || isModer) ? `
                        <div class="row mt-2 mb-3">
                            <div class="col text-end">
                                ${isAuthor ? `<button class="btn btn-primary me-2 edit-comment" data-id="${comment.comment.id}">Редактировать</button>` : ''}
                                ${(isAuthor || isModer) ? `<button class="btn btn-danger delete-comment" data-id="${comment.comment.id}">Удалить</button>` : ''}
                            </div>
                        </div>
                        ` : ''}
                        <hr>
                    </div>
                `;
            });
        }

        fragment.appendChild(container);
        elements.commentHolder.innerHTML = '';
        elements.commentHolder.appendChild(fragment);
    };

    // Инициализация формы комментария
    const initCommentForm = () => {
        if (!elements.commentForm || !elements.commentButton) return;

        elements.commentButton.addEventListener('click', async () => {
            const content = elements.commentTextarea.value.trim();
            if (!content) {
                showError('Комментарий не может быть пустым', elements.commentForm);
                return;
            }

            elements.commentButton.disabled = true;

            try {
                const postId = elements.commentHolder.dataset.postId;
                const response = await fetch(`/comments/${postId}/new`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({content})
                });

                if (!response.ok) throw new Error();

                elements.commentTextarea.value = '';
                await loadComments();
            } catch {
                showError('Ошибка при отправке комментария', elements.commentForm);
            } finally {
                elements.commentButton.disabled = false;
            }
        });
    };

    // Показ ошибок
    const showError = (message, container) => {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger mt-2';
        errorDiv.textContent = message;
        container.prepend(errorDiv);

        // Автоматическое скрытие через 5 секунд
        setTimeout(() => errorDiv.remove(), 5000);
    };

    // Инициализация обработчиков событий
    const initEventHandlers = () => {
        // Используем делегирование событий для динамических элементов
        elements.commentHolder?.addEventListener('click', e => {
            const target = e.target;
            const commentElement = target.closest('.comment-item');

            if (!commentElement) return;

            // Редактирование
            if (target.classList.contains('edit-comment')) {
                startEdit(target.dataset.id, commentElement);
            }
            // Удаление
            else if (target.classList.contains('delete-comment')) {
                deleteComment(target.dataset.id);
            }
        });

        // Обработчики для формы редактирования
        document.addEventListener('click', e => {
            if (e.target.classList.contains('cancel-edit')) {
                cancelEdit();
            } else if (e.target.classList.contains('save-edit')) {
                saveEdit();
            }
        });
    };

    // Запуск приложения
    initEventHandlers();
    initCommentForm();
    loadComments();
});