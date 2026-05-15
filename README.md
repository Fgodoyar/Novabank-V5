# NovaBank Microservices — Reactivo

## Descripción
NovaBank Microservices Reactivo es la evolución del ecosistema del Módulo 4 hacia un stack **no bloqueante** basado en Spring WebFlux, R2DBC y Project Reactor.

El sistema mantiene los mismos dominios de negocio y contratos de API, pero sustituye el modelo imperativo y bloqueante por un modelo reactivo capaz de gestionar miles de peticiones concurrentes con los mismos recursos.

Los microservicios de negocio migrados son:
- **customer-service** → migrado a WebFlux + R2DBC.
- **account-service** → migrado a WebFlux + R2DBC.
- **operation-service** → migrado a WebFlux. Usa WebClient en lugar de Feign. Integra el servicio externo de tipo de cambio.
- **auth-server** → migración parcial a Spring Security reactivo.

Nuevo microservicio añadido en este módulo:
- **exchange-rate-mock-service** → mock reactivo del servicio externo de tipo de cambio, registrado en Eureka como `EXCHANGE-RATE-SERVICE`.

Servicios de infraestructura que **no se migran**:
- **Eureka Server** → servidor de descubrimiento sin lógica de negocio. Se mantiene tal cual.
- **Config Server** → sirve archivos de configuración estáticos. Se mantiene tal cual.
- **API Gateway** → ya era reactivo (Netty + WebFlux) desde el Módulo 4. Sin cambios.

---

# Arquitectura

```text
CLIENTE EXTERNO
       │
       ▼
API GATEWAY (8080)
       │
 ┌─────┼──────┬──────────────────┐
 ▼     ▼      ▼                  ▼
CUSTOMER  ACCOUNT   OPERATION   EXCHANGE-RATE
SERVICE   SERVICE    SERVICE    MOCK-SERVICE
 8081      8082        8083         8084
                        │
                        ▼
               EUREKA SERVER (8761)

CONFIG SERVER (8888)
AUTH SERVER (9000)
```

Cada microservicio de negocio tiene:
- Su propia base de datos PostgreSQL (sin cambios respecto al Módulo 4).
- Sus propias entidades R2DBC y lógica de negocio reactiva.
- Comunicación entre servicios mediante **WebClient** (sustituyendo a Feign).
- Retornos `Mono<T>` o `Flux<T>` en todas las capas.
- Registro automático en Eureka.

---

# Microservicios del sistema

| Servicio | Responsabilidad | Puerto | ¿Migrado? |
|----------|----------------|---------|-----------|
| `customer-service` | Gestión de clientes bancarios | 8081 | Sí — WebFlux + R2DBC |
| `account-service` | Gestión de cuentas y movimientos | 8082 | Sí — WebFlux + R2DBC |
| `operation-service` | Operaciones financieras y tipo de cambio | 8083 | Sí — WebFlux + WebClient |
| `exchange-rate-mock-service` | Mock reactivo del servicio de tipo de cambio | 8084 | Nuevo en Módulo 5 |
| `eureka-server` | Descubrimiento de servicios | 8761 | No — sin cambios |
| `config-server` | Configuración centralizada | 8888 | No — sin cambios |
| `api-gateway` | Enrutamiento y seguridad | 8080 | Ya era reactivo — sin cambios |
| `auth-server` | Emisión y validación JWT | 9000 | Parcial — Spring Security reactivo |

---

# Qué cambia respecto al Módulo 4

| Aspecto | Módulo 4 (bloqueante) | Módulo 5 (reactivo) |
|---------|-----------------------|---------------------|
| Stack web | Spring MVC | Spring WebFlux |
| Persistencia | Spring Data JPA / Hibernate | Spring Data R2DBC |
| Driver de BD | JDBC PostgreSQL | R2DBC PostgreSQL |
| Retorno de controladores | Objetos directos | `Mono<T>` / `Flux<T>` |
| Comunicación entre servicios | Feign Client | WebClient |
| Resiliencia | Resilience4j síncrono | Resilience4j reactivo con anotaciones |
| Tests web | MockMvc / `@WebMvcTest` | WebTestClient / `@WebFluxTest` |
| Tests de servicios | Asserts directos | StepVerifier |
| Tests de cliente | Mock Feign con Mockito | MockWebServer + StepVerifier |
| Tests de repositorio | `@DataJpaTest` | `@DataR2dbcTest` + H2 R2DBC |
| Documentación Swagger | `springdoc-openapi-starter-webmvc-ui` | `springdoc-openapi-starter-webflux-ui` |

---

# Arquitectura interna de cada microservicio

```text
CONTROLLER → SERVICE → REPOSITORY → MODEL
(Mono/Flux)  (Mono/Flux)  (ReactiveCrudRepository)
```

| Capa | Responsabilidad |
|------|----------------|
| `controller` | Endpoints REST; devuelven `Mono<T>` o `Flux<T>` |
| `service` | Lógica de negocio reactiva con operadores de Project Reactor |
| `repository` | Acceso a datos con R2DBC (`ReactiveCrudRepository`) |
| `model` | Entidades del dominio anotadas con `@Table` y `@Id` de Spring Data |
| `dto` | Transferencia de datos (sin cambios respecto al Módulo 4) |
| `client` | Comunicación entre microservicios con WebClient |
| `exception` | Manejo global de errores (`@ControllerAdvice` con retornos `Mono`) |
| `config` | Configuración del servicio y beans de WebClient |

---

# Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring WebFlux**
- **Project Reactor**
- **Spring Data R2DBC**
- **R2DBC PostgreSQL Driver**
- **Spring Cloud**
- **Spring Security** (reactivo)
- **JWT / OAuth2**
- **Spring Cloud Gateway** (sin cambios)
- **Netflix Eureka**
- **Spring Cloud Config**
- **WebClient** (sustituye a OpenFeign)
- **Spring Cloud LoadBalancer**
- **Resilience4j** (con soporte reactivo)
- **PostgreSQL**
- **Swagger / OpenAPI** (`springdoc-openapi-starter-webflux-ui`)
- **JUnit 5**
- **Mockito**
- **StepVerifier** (reactor-test)
- **WebTestClient**
- **MockWebServer** (OkHttp)
- **Maven**

---

# Requisitos

Para ejecutar el proyecto necesitas:

- **JDK 17**
- **Apache Maven 3.8+**
- **PostgreSQL**
- **Git**
- **Postman** (opcional)

---

# Estructura del proyecto

```text
novabank-microservices/
│
├── eureka-server/
├── config-server/
├── api-gateway/
├── auth-server/
├── customer-service/
├── account-service/
├── operation-service/
├── exchange-rate-mock-service/
└── config-repo/
```

---

# Bases de datos

Cada microservicio mantiene su propia base de datos. El contenido no cambia respecto al Módulo 4; lo que cambia es el driver de acceso (R2DBC en lugar de JDBC).

## Crear bases de datos

```sql
CREATE DATABASE novabank_customers;
CREATE DATABASE novabank_accounts;
CREATE DATABASE novabank_operations;
```

---

# Configuración

## Config Server

Las configuraciones residen en el repositorio Git local del Config Server. La configuración de datasource JDBC/JPA se sustituye por la equivalente R2DBC.

Ejemplo para `customer-service.yml`:

```yaml
# Bloque ELIMINADO
# spring:
#   datasource:
#     url: jdbc:postgresql://localhost:5432/novabank_customers
#     username: postgres
#     password: postgres
#   jpa:
#     hibernate:
#       ddl-auto: update

# Bloque AÑADIDO
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/novabank_customers
    username: postgres
    password: postgres
  sql:
    init:
      mode: always  # ejecuta schema.sql sobre el ConnectionFactory R2DBC

server:
  port: 8081
```

---

# Eureka Server

Sin cambios respecto al Módulo 4. Todos los servicios, incluido `exchange-rate-mock-service`, deben aparecer registrados correctamente.

## Acceso al dashboard

```text
http://localhost:8761
```

---

# API Gateway

El Gateway no requiere cambios: ya era reactivo (Netty + WebFlux) desde el Módulo 4.

## Rutas configuradas

| Ruta | Servicio destino |
|------|------------------|
| `/api/customers/**` | CUSTOMER-SERVICE |
| `/api/accounts/**` | ACCOUNT-SERVICE |
| `/api/operations/**` | OPERATION-SERVICE |
| `/api/exchange-rate/**` | EXCHANGE-RATE-SERVICE |
| `/api/auth/**` | AUTH-SERVER |

---

# Autenticación JWT

La autenticación se gestiona mediante `auth-server`, ahora con Spring Security reactivo.

## Diferencias con el Módulo 4

| Módulo 4 (MVC) | Módulo 5 (WebFlux) |
|----------------|---------------------|
| `@EnableWebSecurity` | `@EnableWebFluxSecurity` |
| `HttpSecurity` | `ServerHttpSecurity` |
| `OncePerRequestFilter` | `WebFilter` (devuelve `Mono<Void>`) |
| `SecurityContextHolder` (ThreadLocal) | `ReactiveSecurityContextHolder` |
| `UserDetailsService` | `ReactiveUserDetailsService` |

## Obtener token

```bash
POST http://localhost:8080/api/auth/login
```

Body:

```json
{
  "username": "admin",
  "password": "password"
}
```

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "expiration": 86400000
}
```

## Usar token

```text
Authorization: Bearer <jwt_token>
```

---

# Migración de entidades: de JPA a R2DBC

R2DBC no es un ORM. No gestiona relaciones automáticamente, no tiene lazy loading y no usa las mismas anotaciones que JPA. El modelo de entidades se reescribe completamente.

## Antes

```java
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    private String customerName;
    // ...
}
```

## Después

```java
@Table("customers")
public class Customer {
    @Id  // import org.springframework.data.annotation.Id
    private Long customerId;
    @Column("customer_name")
    private String customerName;
    // ...
}
```

Las relaciones `@OneToMany` / `@ManyToOne` no existen en R2DBC. Dado que cada microservicio tiene su propia base de datos desde el Módulo 4, esto no supone un problema: los datos relacionados se obtienen mediante queries explícitas o llamadas HTTP entre servicios.

---

# Repositorios reactivos con R2DBC

```java
public interface CustomerRepository
        extends ReactiveCrudRepository<Customer, Long> {

    Mono<Customer> findByDni(String dni);
    Mono<Customer> findByEmail(String email);
}
```

`ReactiveCrudRepository` proporciona automáticamente `findById`, `findAll`, `save`, `deleteById`, etc., en versión reactiva. Para consultas complejas se usa `@Query` con SQL nativo.

---

# Comunicación entre microservicios

La comunicación se realiza mediante **WebClient** con balanceo de carga vía Eureka, sustituyendo al Feign Client del Módulo 4.

## Configuración del bean WebClient

```java
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
```

> La dependencia `spring-cloud-starter-loadbalancer` debe declararse explícitamente en el `pom.xml`. En el Módulo 4 llegaba transitivamente a través de Feign; al eliminarlo, desaparece del classpath.

## Antes/después en account-service

**Antes:**

```java
@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{id}")
    CustomerDTO getCustomer(@PathVariable Long id);
}
```

**Después:**

```java
@Component
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://CUSTOMER-SERVICE").build();
    }

    public Mono<CustomerDTO> getCustomer(Long id) {
        return webClient.get()
                .uri("/api/customers/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new CustomerNotFoundException(id)))
                .bodyToMono(CustomerDTO.class);
    }
}
```

Operadores utilizados en los flujos: `flatMap` para encadenar operaciones que devuelven `Mono`, `map` para transformaciones síncronas, `switchIfEmpty` para lanzar excepciones de tipo 404 cuando no existe el recurso.

---

# Resiliencia reactiva con Resilience4j

Las anotaciones `@CircuitBreaker` y `@Retry` de Resilience4j funcionan con métodos que devuelven `Mono` o `Flux` sin configuración adicional, siempre que `spring-cloud-starter-circuitbreaker-resilience4j` esté en el classpath.

```java
@CircuitBreaker(name = "accountService", fallbackMethod = "getAccountFallback")
public Mono<AccountDTO> getAccountByNumber(String accountNumber) {
    return webClient.get()
            .uri("/api/accounts/number/{number}", accountNumber)
            .retrieve()
            .bodyToMono(AccountDTO.class);
}

public Mono<AccountDTO> getAccountFallback(String accountNumber, Throwable ex) {
    return Mono.error(new RuntimeException("Servicio de cuentas no disponible"));
}
```

El método de fallback debe devolver el mismo tipo reactivo (`Mono<AccountDTO>`) que el método protegido. Para operaciones financieras como actualizar saldo o crear transacción, el fallback también lanza excepción: no tiene sentido devolver un valor neutro cuando la operación no se ha completado.

---

# Consulta de tipo de cambio con WebClient

Cuando se realiza una transferencia en moneda extranjera, `operation-service` consulta `exchange-rate-mock-service` de forma no bloqueante antes de ejecutar la operación.

## Endpoint del servicio mock

```text
GET /api/exchange-rate?from=USD&to=EUR
```

Respuesta:

```json
{
  "from": "USD",
  "to": "EUR",
  "rate": 0.917431,
  "date": "2026-05-15T16:44:59.063380100Z"
}
```

## Política de fallback para el tipo de cambio

Aplicar un valor por defecto inventado (como `1.0`) si el servicio falla es un anti-patrón peligroso en cualquier sistema financiero: una transferencia de 100 USD se aplicaría como 100 EUR, generando una diferencia económica real.

La estrategia implementada es la más sencilla y segura: si la consulta al servicio externo falla, la transferencia se aborta con HTTP 503 y un mensaje descriptivo, sin modificar ningún saldo.

```text
503 Service Unavailable
"Tipo de cambio no disponible para USD -> EUR. Operación abortada."
```

---

# Endpoints principales

| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| POST | `/api/auth/login` | Obtener JWT |
| POST | `/api/customers` | Crear cliente |
| GET | `/api/customers` | Listar clientes |
| GET | `/api/customers/{id}` | Obtener cliente |
| POST | `/api/accounts` | Crear cuenta |
| GET | `/api/accounts/{id}` | Obtener cuenta |
| GET | `/api/accounts/number/{number}` | Obtener cuenta por número |
| GET | `/api/accounts/{id}/transactions` | Historial de transacciones |
| POST | `/api/operations/deposit` | Realizar depósito |
| POST | `/api/operations/withdraw` | Realizar retiro |
| POST | `/api/operations/transfer` | Transferencia (EUR o divisa extranjera) |
| GET | `/api/exchange-rate` | Consultar tipo de cambio (mock) |

---

# Ejecución del sistema

## Compilar proyecto

```bash
mvn clean install
```

## Orden de arranque

1. `eureka-server`
2. `config-server`
3. `auth-server`
4. `exchange-rate-mock-service`
5. `customer-service`
6. `account-service`
7. `operation-service`
8. `api-gateway`

## Arrancar cada servicio

```bash
cd eureka-server
mvn spring-boot:run
```

Repetir para cada microservicio en el orden indicado.

---

# Testing

El proyecto incluye tests de servicios con StepVerifier, tests de controladores con WebTestClient y `@WebFluxTest`, tests de repositorios con `@DataR2dbcTest` + H2 R2DBC, y tests de clientes HTTP con MockWebServer.

## Ejecutar tests

```bash
mvn test
```

## Ejemplo: test de controlador con WebTestClient

```java
@WebFluxTest(OperationController.class)
@WithMockUser
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "spring.config.import=",
    "spring.main.allow-bean-definition-overriding=true"
})
public class OperationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OperationService operationService;

    @Test
    @DisplayName("POST /api/operations/deposit → 200 con transacción creada")
    void deposit_shouldReturn200WhenValid() {
        when(operationService.deposit(any(CreateOperationRequest.class)))
                .thenReturn(Mono.just(transactionDTO));

        webTestClient.post().uri("/api/operations/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(VALID_OPERATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transactionId").isEqualTo(1L);
    }

    @Test
    @DisplayName("POST /api/operations/transfer → 422 si saldo insuficiente")
    void transfer_shouldReturn422WhenInsufficientBalance() {
        when(operationService.transfer(any(CreateTransferRequest.class)))
                .thenReturn(Flux.error(new InsufficientBalanceException(...)));

        webTestClient.post().uri("/api/operations/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(VALID_TRANSFER_JSON)
                .exchange()
                .expectStatus().isEqualTo(422);
    }
}
```

## Ejemplo: test de servicio con StepVerifier

```java
@Test
void obtenerCliente_cuandoNoExiste_debeEmitirError() {
    when(customerRepository.findById(99L)).thenReturn(Mono.empty());

    StepVerifier.create(customerService.getCustomer(99L))
            .expectError(CustomerNotFoundException.class)
            .verify();
}
```

---

# Swagger / OpenAPI

Cada microservicio expone documentación Swagger mediante `springdoc-openapi-starter-webflux-ui`.

```text
http://localhost:8081/swagger-ui.html  ← customer-service
http://localhost:8082/swagger-ui.html  ← account-service
http://localhost:8083/swagger-ui.html  ← operation-service
```

---

# Patrones de diseño utilizados

- **Microservices Architecture**
- **API Gateway**
- **Service Discovery**
- **Circuit Breaker** (reactivo)
- **Retry** (reactivo)
- **Fallback** (diferenciado por tipo de operación)
- **Repository** (reactivo con R2DBC)
- **DTO / Mapper**
- **Reactive Streams**
- **Singleton**
- **Dependency Injection**

---

# Limitaciones actuales

- La consistencia distribuida en transferencias sigue dependiendo de llamadas HTTP; no es completamente atómica entre microservicios (se resolverá en el Módulo 6 con SAGA + Kafka).
- Spring Authorization Server no tiene variante reactiva; si el `auth-server` usaba ese framework, se mantiene en MVC.
- WebClient es más verboso que Feign: cada llamada HTTP requiere más código, aunque a cambio se obtiene control total sobre timeouts, manejo de errores por código de estado y la posibilidad de procesar streams de respuesta.

---

# Lecciones aprendidas

La migración de JPA a R2DBC es el cambio más disruptivo del módulo. Perder las relaciones automáticas y el lazy loading obliga a repensar completamente cómo se obtienen datos relacionados.

WebClient es más verboso que Feign pero más potente. Cada llamada HTTP requiere más código, pero a cambio se obtiene composición reactiva nativa y control granular de errores.

Dificultades encontradas durante el desarrollo:

- Conflictos de versiones Jackson entre springdoc 3.x y Spring Boot 3.2.x, resueltos bajando springdoc a la versión 2.x.
- Conflictos al migrar `operation-service`, `account-service` y `auth-service` al stack reactivo.
- Conflictos al migrar los tests existentes.
- Conflictos con el filtro del API Gateway.
- Dificultades al migrar el método `transfer`.
- Error que provocaba que al crear una cuenta el titular apareciera como `null`.
- Errores al realizar transferencias entre divisas.

---

# Futuras mejoras

- Comunicación asíncrona y persistente con **Kafka** (patrón SAGA).
- Caché de tipo de cambio con TTL configurable.
- **Docker Compose** para levantar el ecosistema completo.
- **Kubernetes** para orquestación en producción.
- Observabilidad distribuida con **Zipkin** y **Prometheus**.
- Tests con **Testcontainers** para eliminar la dependencia de PostgreSQL local en CI.
