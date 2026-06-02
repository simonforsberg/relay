# Relay

A distributed chat application with **BFF**, **microservices**, **gRPC** and **event-based architecture**.

## Testing the API

The API can be tested using [Insomnia](https://insomnia.rest/) or any HTTP client.

### Authentication

The BFF (`localhost:8080`) uses session-based OAuth2 — all requests through it
require a valid session cookie. To get one:

1. Log into the chat at `http://localhost:8080`
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
POST http://localhost:8080/api/users
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
