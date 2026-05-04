📦 User Service — Module 3 (Testing)

Расширение проекта User Service с добавлением:

Unit-тестов (Mockito)
Интеграционных тестов (Testcontainers)
DAO-тестов (H2 / Test DB)
🧪 Типы тестов
✅ Unit tests (Service layer)
Используется JUnit 5 + Mockito
Репозиторий полностью замокан
Проверяется бизнес-логика сервиса
База данных НЕ используется

📌 Проверяется:

валидация входных данных
обработка исключений
бизнес-правила
работа с DTO
🧪 Integration tests (Repository / DAO layer)
Используется H2 in-memory database
Hibernate работает в реальном режиме
Проверяются SQL операции через ORM

📌 Проверяется:

CRUD операции
уникальность email
поиск по id / email
обновление и удаление данных
🐳 Testcontainers tests (real PostgreSQL)
Используется Docker контейнер PostgreSQL
Полная эмуляция реальной базы данных
Максимально приближено к production

📌 Проверяется:

работа Hibernate с PostgreSQL
реальные транзакции
constraints (unique, not null)
🧱 Структура тестов
src/test/java
├── controller/     → Unit tests (Mockito)
├── service/        → Unit tests (Mockito)
├── repository/     → H2 integration tests
├── repository/     → Testcontainers tests
🛠️ Технологии
Java 17
JUnit 5
Mockito
Hibernate ORM
H2 Database
PostgreSQL
Testcontainers
Maven
🔄 Изоляция тестов

Каждый тест полностью изолирован:

✔ Mockito изолирует сервисный слой
✔ H2 используется для DAO тестов
✔ Testcontainers поднимает отдельный PostgreSQL
✔ база очищается перед каждым тестом

Пример очистки:

@BeforeEach
void cleanDb() {
session.createMutationQuery("DELETE FROM User").executeUpdate();
}
🐳 Testcontainers (PostgreSQL)

Контейнер запускается автоматически:

@Container
static PostgreSQLContainer<?> postgres =
new PostgreSQLContainer<>("postgres:15")
.withDatabaseName("testdb")
.withUsername("test")
.withPassword("test");
▶️ Запуск тестов
mvn clean test
⚙️ Требования окружения
Docker обязательно:
docker ps
Java версия:
Java 17+
⚠️ Возможные проблемы
❌ Docker не найден

Решение:

запустить Docker Desktop
проверить docker ps
❌ Mockito не работает

Причина:

несовместимая версия Java

Решение:

использовать Java 17 или 21
❌ EntityManagerFactory is closed

Причина:

неправильное управление Hibernate SessionFactory
📌 Особенности реализации
Чистая архитектура (Controller → Service → Repository)
Dependency Injection через конструкторы
DTO разделяют слои
Полная изоляция тестов
Реальные и in-memory базы данных для разных уровней тестирования
🧠 Цель задания

Показать умение:

писать unit-тесты (Mockito)
писать интеграционные тесты (Testcontainers)
разделять уровни приложения
изолировать тесты
работать с Hibernate и БД
📌 Автор

Учебный проект Java backend разработки с акцентом на тестирование и архитектуру.

