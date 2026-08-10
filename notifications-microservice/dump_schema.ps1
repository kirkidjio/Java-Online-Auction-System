$env:PGPASSWORD = "postgres"

$DOCKER_CONTAINER = "postgres-local"
$DB_HOST = "localhost"
$DB_USER = "postgres"
$DB_NAME = "etorg"
$SCHEMA_NAME = "notifications"
$OUTPUT_FILE = "src\main\resources\db\schema_current.sql"

# 1. Запуск pg_dump внутри Docker (ВАЖНО: убран флаг -t, чтобы не забивать файл TTY-символами)
docker exec $DOCKER_CONTAINER pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME `
    -n $SCHEMA_NAME `
    --schema-only `
    --no-owner `
    --no-privileges `
    --no-comments `
    -T "$SCHEMA_NAME.flyway_schema_history" `
    -T "$SCHEMA_NAME.rev*" > $OUTPUT_FILE

# 2. Постобработка: вырезаем системный мусор PostgreSQL
if ($LASTEXITCODE -eq 0 -and (Test-Path $OUTPUT_FILE)) {
    $cleanContent = Get-Content $OUTPUT_FILE | Where-Object {
        $_ -notmatch '^\s*$' -and                           # Пустые строки
        $_ -notmatch '^--' -and                            # Комментарии
        $_ -notmatch '^\\' -and                            # Мета-команды (\restrict, \unrestrict)
        $_ -notmatch '^SET ' -and                          # Настройки сессии (SET statement_timeout)
        $_ -notmatch '^SELECT pg_catalog' -and             # Поисковый путь search_path
        $_ -notmatch '^CREATE SCHEMA' -and                 # Создание самой схемы
        $_ -notmatch '^CREATE SEQUENCE' -and               # Сиквенсы Hibernate Envers
        $_ -notmatch '^ALTER SEQUENCE'                     # Сиквенсы Hibernate Envers
    }

    # 3. Перезаписываем файл в чистом UTF-8
    $cleanContent | Set-Content $OUTPUT_FILE -Encoding UTF8
    Write-Host "Sukces dump" -ForegroundColor Green
} else {
    Write-Host "Failed dump" -ForegroundColor Red
}