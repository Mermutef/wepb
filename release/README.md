# Веб-приложения «Анфас» и «Силуэт»

## Развертывание

1. Заполните файл ```app.properties.example``` необходимыми данными:
   * ```db.user``` - имя пользователя базы данных 
   * ```db.password``` - пароль пользователя базы данных
   * ```db.host``` = db - неизменяемо, если не менялся файл ```compose.yml```
   * ```db.port``` = 5432 - неизменяемо, если не менялся файл ```compose.yml```
   * ```db.base``` - имя базы данных
   * ```auth.generalPass``` - пароль администратора системы
   * ```auth.salt``` - закрытый ключ шифрования паролей ("соль")
   * ```auth.secret``` - закрытый ключ шифрования токенов авторизации
   * ```web.port``` - порт, по которому будет работать приложение
2. Заполните файл ```.env.example``` необходимыми данными:
   * ```POSTGRESDB_USER``` - должен совпадать с ```db.user```
   * ```POSTGRESDB_ROOT_PASSWORD``` - должен совпадать с ```db.password```
   * ```POSTGRESDB_DATABASE``` - должен совпадать с ```db.base```
   * ```APP_LOCAL_PORT``` - должен совпадать с ```web.port```
   * ```APP_DOCKER_PORT``` - внутренний порт приложения в docker-сети
3. Выполните сборку приложения
4. Запустите приложение:
   ```bash
   docker compose up -d
   ```

## [Резервное копирование и восстановление](https://github.com/prodrigestivill/docker-postgres-backup-local)

*Резервное копирование совершается каждые 5 минут. Копии хранятся в директории ```pgbackups``` в корне ```release``` директории*

### Ручное резервное копирование
   ```bash
   docker run --rm -v "$PWD:/backups" -u "$(id -u):$(id -g)" -e POSTGRES_HOST=db -e POSTGRES_DB=<DATABASE_NAME> -e POSTGRES_USER=<DATABASE_USER> -e POSTGRES_PASSWORD=<DATABASE_PASSWORD>  prodrigestivill/postgres-backup-local /backup.sh
   ```

### Восстановление
1) Остановить контейнер ```db```
2) Удалить или переименовать ```pgdata```
3) ```bash
   docker compose up -d --build db
   ```
4) Скопировать файл бэкапа в создавшийся каталог ```pgdata```
5) ```bash
   docker exec --tty --interactive <CONTAINER> /bin/sh -c "zcat /var/lib/postgresql/data/<BACKUP_FILE> | psql --username=<DATABASE_USER> --dbname=<DATABASE_NAME> -W"
   ```
6) ```bash
   docker compose up -d --build web
   ```