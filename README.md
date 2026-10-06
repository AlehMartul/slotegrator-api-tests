# Slotegrator API Tests

REST API test automation for the QA Automation Engineer assignment.

## Tech stack

- Java 21
- REST Assured
- JUnit 5
- AssertJ
- Jackson
- Maven

## Covered scenario

The test covers the complete player lifecycle:

1. Login and obtain an access token.
2. Create 12 players.
3. Get a created player's profile.
4. Get all players and sort them by name.
5. Delete all created players.
6. Verify that the player list is empty.

Created test data is cleaned up even if the test fails.

## Configuration

Copy:

```text
src/test/resources/config.properties.example
```

to:

```text
src/test/resources/config.properties
```

and provide the required credentials.

The real `config.properties` is excluded from Git.

## Run

```bash
mvn clean test
```

## API discrepancies

During testing, several differences between the provided contract and the actual API were found:

- `POST /api/tester/login`
  - Assignment/OpenAPI: HTTP 200.
  - Actual API: HTTP 201.
  - OpenAPI documents `access_token`; actual response contains `accessToken`.

- `POST /api/automationTask/getOne`
  - Assignment: HTTP 200.
  - OpenAPI and actual API: HTTP 201.

- Player ID
  - OpenAPI documents `id`.
  - Create response returns `_id`, while getOne/getAll return `id`.
  - Actual IDs are strings.

- `DELETE /api/automationTask/deleteOne/{id}`
  - OpenAPI describes `id` as an integer.
  - The actual API accepts the string ID returned when a player is created.

- Create/delete responses expose `password_change`, `password_repeat`
  and an undocumented `__v` field. These fields are intentionally not
  included in the response DTO.

Tests assert the actual behavior of the provided test environment.