# Веб-приложение «Инфопорт»

## Разработка

Для разработки рекомендуется использовать Unix-подобную OS.

### Подготовка СУБД
1. Запустите сервер PostgreSQL. Рекомендуемый вариант — [контейнер](https://github.com/docker-library/docs/blob/master/postgres/README.md) в docker).
    ```bash
    docker run -d --name pedb -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:16.3
    ```
2. Создайте на сервере базу данных `pe_project`
   ```bash
   psql -h localhost -U postgres
   ```
   ```bash
   CREATE DATABASE pe_project;
    ```

### Запуск и остановка контейнеров
Для повторных запусков контейнеров (после выключения или перезагрузки системы):
```bash
docker start pedb
```

Для остановки контейнера:
```bash
docker stop pedb
```

### Настройка параметров запуска
1. Создайте копию файла `app.properties.example` с именем `app.properties`.
2. В случае, если вы меняли какие-то параметры СУБД, соответствующим образом отредактируйте `app.properties`.

### Запуск приложения
Выполните задачу
```bash
./gradlew run
```

### Сборка приложения
Выполните задачу
```bash
./gradlew build
```
Будет собран ```fatJar```, содержащий все зависимости. Будет находиться в ```/build/libs/pe-project-all.jar```

### Выполнение тестов
```bash
./gradlew test --rerun-tasks
```
**Обратите внимание:** тесты используют testcontainers, поэтому во время выполнения тестов должен быть запущен docker!

### Очистка базы данных
```bash
./gradlew flywayClean
```
