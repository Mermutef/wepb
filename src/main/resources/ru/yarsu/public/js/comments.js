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

            // Получаем готовый HTML от сервера
            const html = await response.text();

            // Создаем временный контейнер для обработки HTML
            const tempContainer = document.createElement('div');
            tempContainer.innerHTML = html;

            // Удаляем старые комментарии
            document.querySelectorAll('.comment-item, .no-comments').forEach(el => el.remove());

            // Вставляем новые комментарии перед comment-holder
            const newComments = Array.from(tempContainer.children);
            newComments.forEach(comment => {
                commentHolder.parentNode.insertBefore(comment, commentHolder);
            });

        } catch (error) {
            console.error('Ошибка:', error);
            // Показываем сообщение об ошибке
            const errorElement = document.createElement('div');
            errorElement.className = 'alert alert-danger mt-4';
            errorElement.textContent = 'Не удалось загрузить комментарии';
            commentHolder.parentNode.insertBefore(errorElement, commentHolder);
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