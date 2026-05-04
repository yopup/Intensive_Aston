📦 Module 3 — Testing (JUnit + Mockito + Testcontainers)

Расширение проекта user-service с добавлением unit и интеграционных тестов.

🧪 Типы тестов
✅ Unit-тесты (Service слой)
Используется Mockito
Репозиторий мокается
Проверяется бизнес-логика
✅ Интеграционные тесты (DAO слой)
Используется Testcontainers
Поднимается реальный PostgreSQL в Docker
Проверяется работа с БД
🛠️ Технологии
JUnit 5
Mockito
Testcontainers
PostgreSQL
Hibernate
🧱 Структура тестов
test/
├── controller/
├── service/        ← unit тесты (Mockito)
├── repository/     ← интеграционные тесты
🔄 Изоляция тестов

Каждый тест:

использует отдельную БД (Testcontainers / H2)
очищает данные перед выполнением
@BeforeEach
void cleanDb() {
session.createMutationQuery("DELETE FROM User").executeUpdate();
}
🐳 Testcontainers

Пример контейнера:

@Container
static PostgreSQLContainer<?> postgres =
new PostgreSQLContainer<>("postgres:15");
▶️ Запуск тестов
mvn clean test
⚠️ Возможные ошибки

❌ Docker не запущен
→ запусти Docker Desktop

❌ Java версия несовместима
→ используй Java 17 или 21

📌 Особенности
Полная изоляция тестов
Реальная БД для интеграционных тестов
Моки для unit-тестов
Проверка исключений и edge-case

