# API Documentation

## Overview

The MemoApp backend provides a RESTful API for managing memos. All endpoints are prefixed with `/api` and follow REST conventions.

**Base URL**: `http://localhost:1919/api`

## Authentication

Currently, the API does not require authentication. All endpoints are publicly accessible.

## Data Models

### Memo Entity

```json
{
  "id": 1,
  "title": "Sample Memo",
  "content": "This is the content of the memo",
  "createdAt": "2024-03-01T10:30:00",
  "updatedAt": "2024-03-01T10:30:00"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | Long | No (auto-generated) | Unique identifier for the memo |
| `title` | String | Yes | Title of the memo (max 255 characters) |
| `content` | String | No | Content body of the memo (max 10,000 characters) |
| `createdAt` | LocalDateTime | No (auto-generated) | Timestamp when memo was created |
| `updatedAt` | LocalDateTime | No (auto-generated) | Timestamp when memo was last updated |

### Error Response

```json
{
  "timestamp": "2024-03-01T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Memo title cannot be null or empty",
  "path": "/api/memos"
}
```

## Endpoints

### 1. Get All Memos

Retrieve a list of all memos in the system.

**Request:**
```http
GET /api/memos
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "title": "First Memo",
    "content": "Content of first memo",
    "createdAt": "2024-03-01T10:30:00",
    "updatedAt": "2024-03-01T10:30:00"
  },
  {
    "id": 2,
    "title": "Second Memo",
    "content": "Content of second memo",
    "createdAt": "2024-03-01T11:00:00",
    "updatedAt": "2024-03-01T11:00:00"
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:1919/api/memos
```

---

### 2. Get Memo by ID

Retrieve a specific memo by its unique identifier.

**Request:**
```http
GET /api/memos/{id}
```

**Path Parameters:**
- `id` (Long): The unique identifier of the memo

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "title": "First Memo",
  "content": "Content of first memo",
  "createdAt": "2024-03-01T10:30:00",
  "updatedAt": "2024-03-01T10:30:00"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid ID format
- `404 Not Found`: Memo with specified ID does not exist

**cURL Example:**
```bash
curl -X GET http://localhost:1919/api/memos/1
```

---

### 3. Create New Memo

Create a new memo in the system.

**Request:**
```http
POST /api/memos
Content-Type: application/json

{
  "title": "New Memo Title",
  "content": "New memo content"
}
```

**Request Body:**
```json
{
  "title": "New Memo Title",
  "content": "New memo content"
}
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 3,
  "title": "New Memo Title",
  "content": "New memo content",
  "createdAt": "2024-03-01T12:00:00",
  "updatedAt": "2024-03-01T12:00:00"
}
```

**Validation Rules:**
- `title`: Required, non-empty, max 255 characters
- `content`: Optional, max 10,000 characters

**Error Responses:**
- `400 Bad Request`: Invalid request body or validation errors

**cURL Example:**
```bash
curl -X POST http://localhost:1919/api/memos \
  -H "Content-Type: application/json" \
  -d '{"title":"My New Memo","content":"This is the content"}'
```

---

### 4. Update Existing Memo

Update an existing memo by its ID.

**Request:**
```http
PUT /api/memos/{id}
Content-Type: application/json

{
  "title": "Updated Memo Title",
  "content": "Updated memo content"
}
```

**Path Parameters:**
- `id` (Long): The unique identifier of the memo to update

**Request Body:**
```json
{
  "title": "Updated Memo Title",
  "content": "Updated memo content"
}
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "title": "Updated Memo Title",
  "content": "Updated memo content",
  "createdAt": "2024-03-01T10:30:00",
  "updatedAt": "2024-03-01T12:30:00"
}
```

**Validation Rules:**
- Same validation rules as create memo
- Memo must exist (ID validation)

**Error Responses:**
- `400 Bad Request`: Invalid ID or request body
- `404 Not Found`: Memo with specified ID does not exist

**cURL Example:**
```bash
curl -X PUT http://localhost:1919/api/memos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Title","content":"Updated content"}'
```

---

### 5. Delete Memo

Delete a memo by its ID.

**Request:**
```http
DELETE /api/memos/{id}
```

**Path Parameters:**
- `id` (Long): The unique identifier of the memo to delete

**Response:**
```http
HTTP/1.1 204 No Content
```

**Error Responses:**
- `400 Bad Request`: Invalid ID format
- `404 Not Found`: Memo with specified ID does not exist

**cURL Example:**
```bash
curl -X DELETE http://localhost:1919/api/memos/1
```

## Error Handling

The API uses standard HTTP status codes and provides detailed error messages:

### Status Codes

- `200 OK`: Request successful
- `204 No Content`: Delete operation successful
- `400 Bad Request`: Invalid request or validation error
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### Error Response Format

All error responses follow a consistent format:

```json
{
  "timestamp": "2024-03-01T12:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/memos/invalid-id"
}
```

### Common Validation Errors

#### Title Validation
- `"Memo title cannot be null or empty"`
- `"Memo title cannot exceed 255 characters"`

#### Content Validation
- `"Memo content cannot exceed 10,000 characters"`

#### ID Validation
- `"Memo ID cannot be null"`
- `"Memo ID must be a positive number"`
- `"Memo with ID {id} not found"`

## Rate Limiting

Currently, no rate limiting is implemented. Consider implementing rate limiting for production use.

## CORS Configuration

The API is configured to allow cross-origin requests from:
- `http://localhost:6565` (Angular development server)
- `http://localhost:3000` (Alternative frontend port)

## Response Headers

All responses include standard headers:
- `Content-Type: application/json`
- `Access-Control-Allow-Origin: *` (development only)

## Testing the API

### Using curl

All examples above use curl for testing. Ensure the backend server is running on port 1919.

### Using Postman

Import the following collection:

```json
{
  "info": {
    "name": "MemoApp API",
    "version": "1.0"
  },
  "item": [
    {
      "name": "Get All Memos",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/memos"
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:1919"
    }
  ]
}
```

### Integration Tests

The backend includes comprehensive integration tests located in:
- `/MemoApp_Backend/app/src/test/java/memoapp/controller/MemoControllerTest.java`

Run tests with:
```bash
./gradlew test
```