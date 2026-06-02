# relay/

A distributed real-time chat application built around a microservice architecture, with OAuth2 authentication, gRPC inter-service communication, and a BFF gateway layer.

Built with **Java 25**, **Spring Boot 4**, **Maven**, and **Docker**.

---

## Features

- **Microservice Architecture**: Independently deployable services — BFF, AuthService, UserService, and MessageService.
- **BFF Gateway**: Single entry point for clients using Spring Cloud Gateway with session-based OAuth2 authentication.
- **gRPC Communication**: Typed, efficient inter-service communication using Spring gRPC.
- **OAuth2 Authorization Server**: Dedicated AuthService handling login, token issuance, and session management.
- **RESTful API**: Clean API endpoints for users and messages, exposed through the BFF.
- **PostgreSQL Storage**: Persistent storage for users and messages via Docker-managed Postgres instances.
- **Event-driven Messaging**: MessageService publishes to a RabbitMQ queue (`message-published`) whenever a message is saved, enabling downstream consumers to react to new messages asynchronously.
- **Docker Compose**: One-command local setup with all services and databases containerized.
- **Demo User**: A `demo`/`demo` user is created automatically on startup. Log in straight away without registering.

---

## Architecture

```text
Client
  │
  ▼
BFF (8080) ──── OAuth2 ────► AuthService (9000)
  │                               │
  │ HTTP                          │ REST (user lookup)
  │                               ▼
  ├──────────────────────► UserService (8081, gRPC 9091)
  │                               ▲
  │ HTTP                          │ gRPC
  │                               │
  └──────────────────────► MessageService (8082) ──► RabbitMQ
```

- **BFF** is the single entry point — it authenticates requests via OAuth2 and proxies them to the appropriate service.
- **AuthService** handles login and token issuance, and calls UserService over REST to look up users.
- **MessageService** calls UserService over gRPC to resolve user data when handling messages.
- **UserService** exposes both a REST API (port 8081, consumed by BFF and AuthService) and a gRPC interface (port 9091, consumed by MessageService).
- **MessageService** publishes an event to RabbitMQ whenever a message is saved.

---

## Requirements

- **Java 25**
- **Maven**
- **Docker** and **Docker Compose**

## Setup & Running

### 1. Clone the repository
```bash
git clone https://github.com/simonforsberg/relay.git
cd relay
```

### 2. Configure environment variables

The `dev` profile is active by default in all services and uses pre-configured local values. Override with `SPRING_PROFILES_ACTIVE` if needed.

Copy `.env.example` to `.env` and fill in the values — Docker Compose reads this file automatically.

```bash
cp .env.example .env
```

`POSTGRES_PASSWORD` is always required; set it to `postgres` when using the `dev` profile.


### 3. Start infrastructure
```bash
docker compose up -d
```

This starts PostgreSQL (userdb on `5432`, messagedb on `5433`) and RabbitMQ (`5672`, management UI on `15672`).

### 4. Run the services

Start each service with the `dev` profile active (set via `SPRING_PROFILES_ACTIVE=dev` or run configuration):

- `AuthServiceApplication` — port `9000`
- `UserServiceApplication` — port `8081`
- `MessageServiceApplication` — port `8082`
- `BffApplication` — port `8080`

**IDE:** Run each main class directly with the `dev` profile.

**Maven:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Access the application

- **Chat UI**: [http://localhost:8080](http://localhost:8080)
- **RabbitMQ Management**: [http://localhost:15672](http://localhost:15672) (`guest`/`guest`)

---

## Testing the API

The API can be tested using [Insomnia](https://insomnia.rest/) or any HTTP client.

### Authentication

The BFF (`localhost:8080`) uses session-based OAuth2 — all requests through it
require a valid session cookie. To get one:

1. Log into the chat at `http://localhost:8080` with demo user credentials (`demo`/`demo`)
2. Open DevTools → Network → click any request to `localhost:8080`
3. Copy the `JSESSIONID` value from the Cookie header
4. Add it to your requests: `Cookie: JSESSIONID=<value>`

The session is invalidated on logout or server restart, so you may need to
repeat this step.

### Endpoints

Public endpoints (no auth required) can be called directly on UserService:

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `http://localhost:8081/api/users` | Register a new user |
| `GET` | `http://localhost:8081/api/users/by-username?username={username}` | Look up a user by username |

All other endpoints go through the BFF and require the session cookie:

| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `http://localhost:8080/api/users` | Get all users |
| `GET` | `http://localhost:8080/api/users/{id}` | Get user by ID |
| `GET` | `http://localhost:8080/api/users/me` | Get the currently logged-in user |
| `PUT` | `http://localhost:8080/api/users/{id}` | Update a user |
| `DELETE` | `http://localhost:8080/api/users/{id}` | Delete a user |
| `GET` | `http://localhost:8080/api/messages` | Get all messages |
| `GET` | `http://localhost:8080/api/messages/{id}` | Get message by ID |
| `GET` | `http://localhost:8080/api/messages/sender/{senderId}` | Get messages by sender |
| `POST` | `http://localhost:8080/api/messages` | Send a message |

### Request bodies

**Register a user** — `POST /api/users`
```http
POST http://localhost:8081/api/users
Content-Type: application/json

{
  "username": "ron-burgundy",
  "password": "password123",
  "email": "ron@example.com"
}
```

**Update a user** — `PUT /api/users/{id}`

```http
PUT http://localhost:8080/api/users/{id}
Content-Type: application/json
Cookie: JSESSIONID=<value>

{
  "username": "ron-burgundy_updated",
  "password": "newpassword123",
  "email": "ron_new@example.com"
}
```

**Send a message** — `POST /api/messages`

```http
POST http://localhost:8080/api/messages
Content-Type: application/json
Cookie: JSESSIONID=<value>

{
  "content": "Stay classy!"
}
```
