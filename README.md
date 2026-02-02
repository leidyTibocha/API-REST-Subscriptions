# API REST - Subscriptions (music_play)

**Descripción**

Este proyecto implementa una API REST simple para gestionar suscripciones de usuarios (crear, cambiar plan, cancelar y listar). Está desarrollada con Spring Boot y JPA.

**Documentación completa:** [docs/PROJECT_DOCUMENTATION.md](docs/PROJECT_DOCUMENTATION.md)

---

##  Requisitos

- Java 21
- Maven (se incluye wrapper `mvnw` / `mvnw.cmd`)
- PostgreSQL (opcional para desarrollo, la configuración está en `src/main/resources/application-dev.properties`)
- H2 (in-memory) se utiliza en el perfil de tests para ejecutar la suite sin depender de una BD externa

---

##  Ejecutar (Windows)

1. Construir:

```powershell
./mvnw.cmd clean package
```

2. Ejecutar:

```powershell
./mvnw.cmd spring-boot:run
```

Si usas Docker/compose, revisa `compose.yaml` para levantar dependencias (p. ej. Postgres).

**Swagger / OpenAPI**: Una vez la aplicación esté corriendo, abre `http://localhost:8080/swagger-ui.html` (o `http://localhost:8080/swagger-ui/index.html` según versión) para acceder a la documentación interactiva. La especificación JSON está disponible en `GET /v3/api-docs`.

##  Docker - build & run
Se incluye un `Dockerfile` para facilitar el despliegue en la nube o en entornos de contenedores.

- Construir imagen:

```powershell
docker build -t musicplay/subscriptions:1.0.0 .
```

- Ejecutar localmente (mapea puerto 8080):

```powershell
docker run -p 8080:8080 --rm musicplay/subscriptions:1.0.0
```

- Pushear a Docker Hub (ejemplo):

```powershell
docker tag musicplay/subscriptions:1.0.0 yourdockerhubuser/musicplay:1.0.0
docker push yourdockerhubuser/musicplay:1.0.0
```

También puedes usar `docker compose` para levantar la base de datos y la app conjuntamente (revisar `compose.yaml`).


---

## Endpoints

| Método | Ruta | Cuerpo | Descripción | Respuesta |
|--------|------|--------|-------------|----------:|
| POST   | `/subscriptions` | `{ "userId": 1, "plan": "PREMIUM" }` | Crear suscripción | **201 Created** / **400 Bad Request** (plan inválido o usuario ya tiene suscripción auto-renovable) |
| PUT    | `/change-plan` | `{ "userId": 1, "newPlan": "FAMILY" }` | Cambiar plan (marca la previa como reemplazada y crea una nueva) | **201 Created** / **400 Bad Request** (plan inválido) / **404 Not Found** (no existe suscripción activa) |
| PUT    | `/cancel/{userId}` | - | Cancelar suscripción activa del usuario | **200 OK** / **404 Not Found** (no existe suscripción) |
| GET    | `/subscriptions/user/{userId}` | - | Obtener suscripción activa de un usuario | **200 OK** / **404 Not Found** |
| GET    | `/all-subscriptions` | - | Obtener todas las suscripciones | **200 OK** |

**Notas:**
- Valores válidos para `plan`: **PREMIUM**, **FAMILY** (en mayúsculas).
- Errores comunes: `InvalidSubscriptionException` → 400 Bad Request; `SubscriptionDoesNotExist` → 404 Not Found; excepciones no controladas → 500 Internal Server Error.
- Se recomienda añadir validaciones (`@NotNull`, `@Valid`) en los DTOs para evitar entradas inválidas.

---

##  Ejemplos (curl)

Crear suscripción (usa el valor exacto del enum `PREMIUM`):

```bash
curl -X POST http://localhost:8080/subscriptions -H "Content-Type: application/json" -d '{"userId":1,"plan":"PREMIUM"}'
```

Cambiar plan:

```bash
curl -X PUT http://localhost:8080/change-plan -H "Content-Type: application/json" -d '{"userId":1,"newPlan":"FAMILY"}'
```

Cancelar suscripción:

```bash
curl -X PUT http://localhost:8080/cancel/1
```

---

##  Notas y configuración adicional

- Perfiles: **dev** (usa PostgreSQL con `application-dev.properties`) y **test** (H2 en memoria para ejecutar la suite de tests sin dependencias externas).
- Para cambiar la configuración de la base de datos en desarrollo, edita `src/main/resources/application-dev.properties` o usa variables de entorno:
  - `SPRING_DATASOURCE_URL` (ej. `jdbc:postgresql://localhost:5432/music_play`)
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`

- Tests: la suite de tests usa H2 en memoria por defecto (ver `src/test/resources/application.properties`). Ejecutar:

```powershell
./mvnw.cmd test
```

- Seguridad: no incluyas credenciales en el repo; usa variables de entorno o secretos en CI.

---

---

##  Ejemplos (curl)

Crear suscripción:

```bash
curl -X POST http://localhost:8080/subscriptions -H "Content-Type: application/json" -d '{"userId":1,"plan":"PREMIUM"}'
```

Cambiar plan:

```bash
curl -X PUT http://localhost:8080/change-plan -H "Content-Type: application/json" -d '{"userId":1,"newPlan":"FAMILY"}'
```

Cancelar suscripción:

```bash
curl -X PUT http://localhost:8080/cancel/1
```

---

##  Tests

Ejecutar pruebas unitarias:

```powershell
./mvnw.cmd test
```

---

##  Notas

- La API utiliza Java 21 y Spring Boot 4.x.
- Las clases principales cuentan ahora con Javadoc para facilitar su comprensión (controlador, DTOs y service).

---

