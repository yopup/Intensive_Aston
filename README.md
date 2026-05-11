# 📦 User Service — Module 4 (Spring Boot REST API)

Разработка REST API для управления пользователями с использованием Spring Boot, Spring Data JPA и PostgreSQL.

Проект представляет собой backend-приложение с CRUD-операциями, DTO-архитектурой, валидацией данных, обработкой исключений и тестированием API.

---

# 🚀 Функциональность

Приложение поддерживает:

* Создание пользователя
* Получение пользователя по ID
* Получение списка всех пользователей
* Обновление пользователя
* Удаление пользователя

---

# 🧱 Архитектура проекта

Используется классическая многослойная архитектура:

Controller → Service → Repository → Database

## 📌 Слои приложения

### Controller

Обрабатывает HTTP-запросы и возвращает DTO.

### Service

Содержит бизнес-логику приложения.

### Repository

Работает с базой данных через Spring Data JPA.

### DTO

Используются для передачи данных между клиентом и сервером.

---

# 🛠️ Технологии

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* Hibernate
* Docker
* Lombok
* JUnit 5
* Mockito
* MockMvc
* Testcontainers
* Maven

---

# 🗄️ База данных

В качестве базы данных используется PostgreSQL.

База запускается в Docker контейнере.

---

# 🐳 Запуск PostgreSQL через Docker

## Вариант 1 — docker run

```bash
docker run --name module_4-postgres \
-e POSTGRES_DB=userdb \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=root \
-p 5432:5432 \
-d postgres:15
```

## Вариант 2 — docker-compose

```bash
docker compose up -d
```

---

# ⚙️ Конфигурация приложения

Файл:

```text
src/main/resources/application.yml
```

Пример конфигурации:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: postgres
    password: root

  jpa:
    hibernate:
      ddl-auto: update
```

---

# 📡 REST API

## 🔹 Создать пользователя

### Request

```http
POST /api/users
Content-Type: application/json
```

### JSON

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "age": 25
}
```

---

## 🔹 Получить пользователя по ID

```http
GET /api/users/1
```

---

## 🔹 Получить всех пользователей

```http
GET /api/users
```

---

## 🔹 Обновить пользователя

```http
PUT /api/users/1
Content-Type: application/json
```

### JSON

```json
{
  "name": "Updated User",
  "email": "updated@example.com",
  "age": 30
}
```

---

## 🔹 Удалить пользователя

```http
DELETE /api/users/1
```

---

# ✅ Валидация данных

Для проверки входных данных используются Jakarta Validation annotations.

Примеры:

```java
@NotBlank
@Email
@Min
@Max
```

---

# ⚠️ Обработка ошибок

Используется глобальный обработчик исключений:

```java
@RestControllerAdvice
```

Обрабатываются:

* ошибки валидации
* пользовательские исключения
* внутренние ошибки сервера

---

# 🧪 Тестирование

## ✅ Unit tests

Используется:

* JUnit 5
* Mockito

Проверяется:

* бизнес-логика
* обработка ошибок
* работа сервисного слоя

---

## ✅ API tests

Используется:

* MockMvc

Проверяется:

* HTTP status codes
* JSON responses
* работа REST API

---

## ✅ Integration tests

Используется:

* Testcontainers
* PostgreSQL container

Контейнер запускается автоматически перед тестами.

Пример:

```java
@Container
static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15");
```

---

# ▶️ Запуск приложения

## Сборка проекта

```bash
mvn clean install
```

## Запуск приложения

```bash
mvn spring-boot:run
```

---

# ▶️ Запуск тестов

```bash
mvn test
```

---

# 📂 Структура проекта

```text
src
├── main
│   ├── java/org/example
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── entity
│   │   ├── dto
│   │   ├── exception
│   │   └── UserServiceApplication
│   └── resources
│       └── application.yml
│
└── test
    ├── controller
    ├── service
    └── integration
```

---

# ⚙️ Требования окружения

* Java 17+
* Maven
* Docker

Проверка Docker:

```bash
docker ps
```

---

# ⚠️ Возможные проблемы

## ❌ PostgreSQL connection refused

Причина:

PostgreSQL контейнер не запущен.

Решение:

```bash
docker ps
```

или:

```bash
docker compose up -d
```

---

## ❌ Port 5432 already in use

Причина:

Порт занят другим PostgreSQL.

Решение:

* остановить локальный PostgreSQL
* изменить порт в docker-compose.yml

---

## ❌ Testcontainers не запускается

Причина:

Docker выключен.

Решение:

Запустить Docker Desktop.

---

# 📌 Особенности реализации

* Spring Boot REST API
* DTO вместо Entity в API
* Spring Data JPA
* PostgreSQL + Docker
* Глобальная обработка ошибок
* Валидация данных
* MockMvc тестирование
* Testcontainers интеграционные тесты
* Чистая архитектура

---

# 🧠 Цель задания

Показать умение:

* разрабатывать REST API
* использовать Spring Boot
* работать с Spring Data JPA
* использовать DTO
* писать unit и integration тесты
* работать с PostgreSQL
* использовать Docker
* проектировать backend архитектуру

---

# 📌 Автор

Учебный проект Java backend разработки с использованием Spring Boot, REST API и PostgreSQL.
