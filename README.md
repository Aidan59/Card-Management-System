Card Management System
Это Spring Boot приложение для управления пользователями, картами и транзакциями. Приложение использует PostgreSQL как основную базу данных и может быть полностью запущено локально с помощью Docker.

Технологии
-
Java 21

Spring Boot

PostgreSQL 16

Liquibase для управления миграциями

Docker + Docker Compose

Быстрый старт
-

1. Убедитесь, что у вас установлены:
   Docker: https://www.docker.com/products/docker-desktop,
   Docker Compose


2. Клонируйте репозиторий:
   git clone https://github.com/your-username/card-management-system.git
   cd card-management-system


3. Запустите приложение:
   docker-compose up --build


4. Доступ к приложению:
   API будет доступен по адресу: http://localhost:8080

База данных PostgreSQL будет доступна по адресу: localhost:5432

База: card-management-system

Пользователь: postgres

Пароль: postgres

Тесты
-
Чтобы выполнить юнит- и интеграционные тесты локально (вне Docker), используйте:

./mvnw test

Структура проекта
-
├── src/                 # Исходный код Spring Boot приложения

├── docker-compose.yml  # Конфигурация сервисов (App + PostgreSQL)

├── Dockerfile          # Многоступенчатая сборка образа приложения

├── pom.xml             # Maven конфигурация

└── README.md           # Инструкция