# Delivery Microservices App

Набор микросервисов для управления заказами, оплатой, доставкой и пользователями. Проект на Spring Boot и Gradle, с обменом событиями через Kafka и хранением данных в PostgreSQL.

## Состав проекта

Модули (Gradle):
- `admin-service` — агрегация данных и админские операции. Интегрируется с другими сервисами по HTTP.
- `order-service` — управление заказами.
- `payment-service` — обработка оплат.
- `delivery-service` — управление доставкой.
- `user-service` — пользователи и аутентификация (JWT).
- `common-libs` — общие зависимости и общие типы.

## Технологии

- Java 21
- Spring Boot 3.5.7
- PostgreSQL
- Kafka
- Gradle (Kotlin DSL)
- Swagger/OpenAPI (springdoc)

## Порты сервисов

HTTP:
- `order-service` — `8080`
- `payment-service` — `8081`
- `delivery-service` — `8082`
- `user-service` — `8083`
- `admin-service` — `8084`

Инфраструктура:
- Postgres (orders) — `localhost:5439`
- Postgres (users) — `localhost:5440`
- Kafka — `localhost:9092` (наружу), `kafka:29092` (в docker-сети)
- Kafka UI — `http://localhost:8090`

## События Kafka

- `orders.event` — событие об оплате заказа (`order-paid-topic`)
- `delivery.event` — событие о назначении доставки (`delivery-assigned-topic`)

Настройки топиков и адрес брокера задаются в `application.yml` соответствующих сервисов.

## Быстрый старт

1. Поднять инфраструктуру (PostgreSQL и Kafka):

```bash

docker-compose up -d
```

2. Запустить сервисы (каждый в отдельном терминале):

```bash
./gradlew :order-service:bootRun
./gradlew :payment-service:bootRun
./gradlew :delivery-service:bootRun
./gradlew :user-service:bootRun
./gradlew :admin-service:bootRun
```

Если нужен запуск всех сервисов по очереди, можно использовать несколько терминалов или свой скрипт/оркестрацию.

## Swagger / OpenAPI

Swagger UI доступен по адресу:
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8082/swagger-ui.html`
- `http://localhost:8083/swagger-ui.html`
- `http://localhost:8084/swagger-ui.html`

## Конфигурация

Основные настройки находятся в `application.yml` каждого сервиса:
- `admin-service/src/main/resources/application.yml`
- `order-service/src/main/resources/application.yml`
- `payment-service/src/main/resources/application.yml`
- `delivery-service/src/main/resources/application.yml`
- `user-service/src/main/resources/application.yml`

Там задаются:
- порты `server.port`
- строки подключения к БД
- Kafka `bootstrap-servers`
- темы событий
- URL-адреса внутренних HTTP-интеграций

## Тесты

Запуск всех тестов:

```bash
./gradlew test
```

Или тесты конкретного сервиса:

```bash
./gradlew :user-service:test
```




