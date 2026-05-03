# 📦 User Service (Hibernate + PostgreSQL)

Консольное приложение на Java для управления пользователями с использованием Hibernate ORM и PostgreSQL.

---

## 🚀 Функциональность

Приложение поддерживает полный набор CRUD-операций:

* ✅ Создание пользователя
* 📄 Получение пользователя по ID
* 📋 Получение списка всех пользователей
* ✏️ Обновление пользователя
* ❌ Удаление пользователя

---

## 🛠️ Технологии

* Java 17+
* Hibernate (ORM)
* PostgreSQL
* Maven
* Log4j2 (логирование)
* Docker (для базы данных)

---

## 🗄️ Структура проекта

```bash
Controller → Service → Repository (DAO) → Hibernate → PostgreSQL
```

* **Controller** — обработка входных данных (консоль)
* **Service** — бизнес-логика
* **Repository (DAO)** — работа с базой данных
* **Entity** — модель данных (User)
* **DTO** — передача данных между слоями

---

## 🐳 Запуск PostgreSQL через Docker

Перед запуском приложения необходимо запустить базу данных.

### 1. Запуск контейнера:

```bash
docker run --name module_2-hibernate-postgres \
-e POSTGRES_DB=userdb \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=root \
-p 5432:5432 \
-d postgres
```

---

### 2. Проверка, что контейнер работает:

```bash
docker ps
```

В списке должен быть контейнер `module_2-hibernate-postgres`

---

## ⚙️ Конфигурация базы данных

Настройки подключения находятся в файле:

```bash
hibernate.cfg.xml
```

Параметры:

```xml
jdbc:postgresql://localhost:5432/userdb
username: postgres
password: root
```

---

## ▶️ Запуск приложения

### 1. Сборка проекта:

```bash
mvn clean install
```

---

### 2. Запуск:

Запусти класс:

```bash
org.example.Main
```

---

## 🖥️ Пример работы

После запуска появится меню:

```text
1. Create User
2. Get User by ID
3. Get All Users
4. Update User
5. Delete User
6. Exit
```

---

## 🧠 Особенности реализации

* Использован **Hibernate без Spring**
* Реализован **DAO-паттерн**
* Настроены **транзакции через Hibernate Session**
* Используются **DTO для разделения слоев**
* Добавлена **обработка исключений**
* Поле `created_at` заполняется автоматически (`@PrePersist`)
* Уникальность email обеспечивается на уровне базы данных

---

## ⚠️ Возможные ошибки

### ❌ Ошибка подключения к БД

Проверь:

* запущен ли Docker контейнер
* совпадают ли параметры подключения

---

### ❌ Порт 5432 занят

Останови другой PostgreSQL или измени порт

---

## 📌 Автор

Проект выполнен в рамках обучения Java-разработке.
