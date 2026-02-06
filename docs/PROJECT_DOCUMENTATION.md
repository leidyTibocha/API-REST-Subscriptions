# Documentación del Proyecto - API REST Subscriptions (music_play)

## 1. Resumen

API REST para la gestión de suscripciones de usuarios (crear, cambiar plan, cancelar, consultar y listar). Proyecto modular con las siguientes capas:
- API (controladores y DTOs)
- Application (servicios)
- Domain (modelos y reglas de negocio)
- Infrastructure (persistencia: JPA, mappers y repositorios)

Tecnologías principales: Java 21, Spring Boot 4.x, Spring Data JPA, MapStruct, PostgreSQL (opcional).

---

## 2. Estructura del repositorio 

- `src/main/java/com/musicPlay/music_play/api` : controladores, DTOs y excepciones REST.
- `src/main/java/com/musicPlay/music_play/application` : servicios y lógica de aplicación.
- `src/main/java/com/musicPlay/music_play/domain` : modelos de dominio, enums y excepciones de negocio.
- `src/main/java/com/musicPlay/music_play/infrastructure` : mappers MapStruct, entidades JPA, repositorios.
- `src/test` : pruebas unitarias y de integración.
- `compose.yaml` : orquestación de dependencias (p. ej. Postgres) para desarrollo.

---

## 3. Endpoints principales (Resumen)

| Método | Ruta | Cuerpo | Descripción | Respuesta |
|--------|------|--------|-------------|----------:|
| POST   | `/subscriptions` | `{ "userId": 1, "plan": "PREMIUM" }` | Crear suscripción | **201 Created** / **400 Bad Request** (plan inválido o usuario ya tiene suscripción auto-renovable) |
| PUT    | `/change-plan` | `{ "userId": 1, "newPlan": "FAMILY" }` | Cambiar plan (marca la previa como reemplazada y crea una nueva) | **201 Created** / **400 Bad Request** (plan inválido) / **404 Not Found** (no existe suscripción activa) |
| PUT    | `/cancel/{userId}` | - | Cancelar suscripción activa del usuario | **200 OK** / **404 Not Found** (no existe suscripción) |
| GET    | `/subscriptions/user/{userId}` | - | Obtener suscripción activa de un usuario | **200 OK** / **404 Not Found** |
| GET    | `/all-subscriptions` | - | Obtener todas las suscripciones | **200 OK** |

**Notas:**
- Valores válidos para `plan`: **PREMIUM**, **FAMILY** ( en mayúsculas).
- Errores comunes: `InvalidSubscriptionException` → 400 Bad Request; `SubscriptionDoesNotExist` → 404 Not Found; excepciones no controladas → 500 Internal Server
---

## 3.1 Documentación automática (OpenAPI / Swagger)

Se integra **Springdoc OpenAPI** para exponer la especificación OpenAPI y una UI interactiva (Swagger UI):

- JSON OpenAPI: `GET /v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html` (puede redirigir a `http://localhost:8080/swagger-ui/index.html` según la versión de Springdoc)

La configuración (título, descripción, versión y contacto) está en `OpenApiConfig`.

---

### Validación y contratos de entrada (Inputs & Validation)

- `CreateSubscriptionRequest.userId` — obligatorio.
- `CreateSubscriptionRequest.plan` — obligatorio; valores permitidos: `PREMIUM`, `FAMILY`.
- `ChangePlanRequest.newPlan` — obligatorio; valores permitidos: `PREMIUM`, `FAMILY`.

### Mapeo de errores a HTTP (Error mapping)

- `InvalidSubscriptionException` → 400 Bad Request
- `SubscriptionCannotBeRenewedException` → 400 Bad Request
- `SubscriptionCannotBeCanceledException` → 400 Bad Request
- `SubscriptionDoesNotExist` → 404 Not Found
- Excepciones no controladas → 500 Internal Server Error

## 4. Documentación por capas (detallada) -

A continuación se detalla cada capa del proyecto y responsabilidades:

### 4.1 Capa API (controladores y DTOs)
- `ControllerSubscription` : Exposición de endpoints REST para suscripciones.
- DTOs:
  - `CreateSubscriptionRequest` : Payload para crear suscripciones.
  - `ChangePlanRequest` : Payload para cambiar plan.
  - `SubscriptionResponse` : DTO de salida con detalles de suscripción.
  - `SubscriptionCanceledResponse` : Respuesta para operaciones de cancelación.
- `RestExceptionSubscription` : Mapea excepciones de dominio a respuestas HTTP amigables.

### 4.2 Capa Application (servicios)
- `SubscriptionService` : Orquesta reglas de negocio y llamada a repositorios y mappers. Aquí se realizan las validaciones de existencia, creación, reemplazo y cancelación de suscripciones.

### 4.3 Capa Domain (modelo y reglas de negocio)
- `Subscription` : Objeto de dominio que encapsula invariantes y comportamientos (cancelar, renovar, marcar como reemplazada).
- Enums: `SubscriptionPlan`, `SubscriptionStatus`.
- Excepciones: `InvalidSubscriptionException`, `SubscriptionCannotBeCanceledException`, `SubscriptionCannotBeRenewedException`, `SubscriptionDoesNotExist`.

### 4.4 Capa Infrastructure (persistencia y mapeo)
- `SubscriptionEntity` : Entidad JPA que persiste la suscripción.
- `CrudSubscription` : Interfaz Spring Data JPA para consultas básicas.
- `JpaRepositorySubscription` : Implementa `SubscriptionRepository` y utiliza `MapperSubscription` para conversión entre entidad, dominio y DTOs.
- `MapperSubscription` (MapStruct) : Conversión entre niveles (entidad <-> dominio <-> DTO).

---

(El resto del documento mantiene las secciones previas: Ejecución, Tests.)

## Modelo del dominio y reglas clave 

- `Subscription` (modelo de dominio): encapsula reglas como:
  - Validaciones al crear (userId y plan son obligatorios; plan debe ser uno de los valores del enum `SubscriptionPlan`).
  - **Nota importante:** actualmente el enum contiene `PREMIUM` y `FAMILY`.
  - Estados: `ACTIVE`, `CANCELLED`, `EXPIRED`.
  - Comportamientos: `cancel()`, `renew()`, `isRenewable()`, `markAsReplaced()`.
- Si una operación no cumple las reglas se lanzan excepciones de dominio:
  - `InvalidSubscriptionException` (datos inválidos)
  - `SubscriptionCannotBeCanceledException`
  - `SubscriptionCannotBeRenewedException`
  - `SubscriptionDoesNotExist`

---

## 5. Persistencia y mappers 

- Entidad JPA: `SubscriptionEntity` con campos mapeados a la BD.
- Repositorio CRUD con Spring Data: `CrudSubscription`.
- Repositorio de dominio: `JpaRepositorySubscription` que implementa `SubscriptionRepository` y usa `MapperSubscription` (MapStruct) para convertir entre entidades y dominios/DTOs.

---

## 6. Manejo de errores en la API 

- `RestExceptionSubscription` captura excepciones de dominio y devuelve `ErrorInfo` con mensajes legibles.
- Las reglas de negocio no devuelven HTTP 4xx directamente; el handler traduce las excepciones a respuestas HTTP apropiadas.

---

## 7. Cómo ejecutar 

1. Configura la BD si la vas a usar (ver `src/main/resources/application.properties` o perfiles `application-dev.properties`).
2. Levantar por Maven (Windows):

```powershell
./mvnw.cmd clean package
./mvnw.cmd spring-boot:run
```

3. Con Docker Compose (si prefieres): revisar `compose.yaml` y ejecutar `docker compose up`.

### 7.1 Containerización (Docker) 

Se incluye un `Dockerfile` en la raíz para facilitar la creación de imágenes y el despliegue en la nube.

- Construir imagen (desde la raíz del proyecto):

```bash
docker build -t musicplay/subscriptions:1.0.0 .
```

- Ejecutar localmente (mapear puerto 8080):

```bash
docker run -p 8080:8080 --rm musicplay/subscriptions:1.0.0
```

- Pushear a un registry (ejemplo Docker Hub):

```bash
docker tag musicplay/subscriptions:1.0.0 yourdockerhubuser/musicplay:1.0.0
docker push yourdockerhubuser/musicplay:1.0.0
```

Notas:
- El `Dockerfile` usa multi-stage (imagen Maven para compilación y JRE para ejecución) para mantener la imagen final ligera.
- Si vas a desplegar en un proveedor en la nube, añade variables de entorno (DB, credenciales) y revisa la configuración de healthchecks y recursos.

---

## 8. Tests y CI 

- Ejecutar pruebas locales:

```powershell
./mvnw.cmd test
```

- Nota: La suite de tests ahora puede ejecutarse usando **H2 en memoria** (ver `src/test/resources/application.properties`). Esto evita la dependencia de Postgres en entornos de CI/local; asegúrate de ejecutar los tests con el profile de test o dejar la configuración de tests por defecto como está en el repo.

---

## 9. Guía de contribución rápida 

- Clonar el repo y crear una rama para tu feature/fix.
- Ejecutar `./mvnw.cmd test` y asegurar que todas las pruebas pasen.
- Añadir Javadoc y actualizar la documentación en `docs/PROJECT_DOCUMENTATION.md` si introduce cambios en la API o dominio.


---
