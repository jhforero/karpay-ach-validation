# Karpay - Servicio de Validación de Cuentas Bancarias ACH

Servicio backend para validar cuentas bancarias mediante ACH. Una fintech registra una cuenta, el servicio la envía a validación ACH, almacena el estado y permite consultarlo posteriormente, garantizando idempotencia y trazabilidad.

## Stack

- Java 17+ / Spring Boot 3.3.4
- PostgreSQL 16 (Docker)
- Spring Data JPA / Hibernate
- Spring Security + JWT (jjwt)
- Resilience4j (Circuit Breaker + Retry)
- Maven

## Requisitos previos

- Java 17 o superior
- Maven
- Docker y Docker Compose

## Cómo levantar el proyecto

### 1. Levantar PostgreSQL

```bash
docker compose up -d
```

Esto levanta una base de datos PostgreSQL en el puerto `5440` con la base `karpay`.

### 2. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

O desde el IDE, ejecutando la clase `AchValidationApplication`.

La aplicación arranca en `http://localhost:8080`.

> Nota sobre el esquema: el script `src/main/resources/schema.sql` crea las tablas.
> En el primer arranque se ejecuta el script; luego `spring.jpa.hibernate.ddl-auto=validate`
> verifica que las entidades coincidan con el esquema (JPA no genera DDL por su cuenta).

## Autenticación

La API usa JWT. Primero obtén un token mediante login y envíalo en el header
`Authorization: Bearer <token>` en las peticiones protegidas.

### Usuarios de prueba (en memoria)

| Usuario  | Contraseña   | Rol     |
|----------|--------------|---------|
| admin    | admin123     | ADMIN   |
| fintech  | fintech123   | FINTECH |
| auditor  | auditor123   | AUDITOR |

### Login

```
POST /api/v1/auth/login
Content-Type: application/json

{ "username": "admin", "password": "admin123" }
```

Devuelve: `{ "accessToken": "...", "tokenType": "Bearer" }`

## Endpoints principales

### Crear validación (protegido)

```
POST /api/v1/validations
Authorization: Bearer <token>
Content-Type: application/json

{
  "customerDocument": "12345678",
  "bankCode": "007",
  "accountType": "SAVINGS",
  "accountNumber": "1234567890"
}
```

Idempotencia: opcionalmente se puede enviar el header `Idempotency-Key`. Si no se
envía, el servicio genera un identificador determinístico a partir de los datos de
la solicitud (hash). Una solicitud repetida devuelve la validación existente.

### Consultar validación (protegido)

```
GET /api/v1/validations/{id}
Authorization: Bearer <token>
```

### Webhook de ACH (lo invoca el sistema ACH)

```
POST /api/v1/webhooks/ach
Content-Type: application/json

{ "reference": "ACH12345678", "status": "APPROVED" }
```

Idempotente: ignora eventos repetidos. Registra cada evento recibido para auditoría.

## Decisiones de diseño

- **Idempotencia en 3 capas:** header `Idempotency-Key` (estándar de la industria),
  hash de los datos como respaldo, y constraint `UNIQUE` en base de datos como
  garantía final.
- **Resiliencia del cliente ACH:** timeout, retry y circuit breaker (Resilience4j).
  Si ACH no responde, un fallback controlado evita propagar el fallo.
- **Transaccionalidad:** las operaciones de escritura usan `@Transactional` para
  garantizar atomicidad (o se completa todo o se revierte).
- **JWT stateless:** el token firmado transporta el rol; el servidor no mantiene sesión.
- **DTOs:** la API no expone las entidades JPA directamente.

## Posibles mejoras (producción)

- Migraciones versionadas con Flyway/Liquibase en lugar de `schema.sql`.
- Usuarios persistidos en base de datos (no en memoria).
- Verificación de firma (HMAC) en el webhook de ACH.
- Secretos (jwt.secret, credenciales DB) en variables de entorno / gestor de secretos.
