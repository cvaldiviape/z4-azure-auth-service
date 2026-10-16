# Auth service

Servicio responsable del registro de usuarios, autenticación y generación de JWT.

## Configuración local

| Propiedad | Valor predeterminado |
|---|---|
| Puerto HTTP | `8081` |
| PostgreSQL | `localhost:5433/auth_db` |
| Usuario | `ecommerce` |
| Contraseña | `ecommerce` |

`application.yml` contiene la configuración común y activa `local` como perfil predeterminado. `application-local.yml` agrega la conexión a la base Docker.

Flyway crea y modifica el esquema mediante `db/migration`. Hibernate utiliza `ddl-auto: validate` únicamente para comprobar que las entidades coincidan con las tablas; no crea ni altera la estructura.

La expresión:

```yaml
url: ${AUTH_DB_URL:jdbc:postgresql://localhost:5433/auth_db}
```

utiliza `AUTH_DB_URL` cuando existe; de lo contrario emplea el valor posterior a `:`.

## Variables disponibles

```text
AUTH_DB_URL
AUTH_DB_USERNAME
AUTH_DB_PASSWORD
JWT_SECRET
```

## Ejecutar y depurar

Inicia `auth-postgres` desde la raíz del proyecto y ejecuta `AuthServiceApplication` con **Debug** en IntelliJ.

```bash
docker compose -f infra/docker/docker-compose.yml up -d auth-postgres
```

Para compilar sin ejecutar pruebas:

```bash
./mvnw clean package -DskipTests
```

## Errores de validación

Spring Validation conserva las anotaciones declaradas en los DTO. `GlobalExceptionHandler` transforma sus errores al contrato común de la API:

```json
{
  "code": "VALIDATION_ERROR",
  "statusCode": 400,
  "message": "Request validation failed",
  "data": {
    "password": "Password must contain at least 8 characters"
  }
}
```
