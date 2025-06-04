# Embers http4k Integration

This module demonstrates how to integrate Embers with the [http4k](https://www.http4k.org/) framework.

## Features

- Lightweight http4k-based HTTP server
- Full Embers API support (query, admin, and cache endpoints)
- In-memory H2 database for development
- Clean Kotlin DSL for route definitions
- Integration tests with http4k's test client

## Getting Started

### Prerequisites

- JDK 17 or higher
- Gradle 7.0+

### Running the Example

1. Build the project:
   ```bash
   ./gradlew :embers-http4k:build
   ```

2. Run the application:
   ```bash
   ./gradlew :embers-http4k:run
   ```

3. The server will start on `http://localhost:8002`

### API Endpoints

- `GET /query/{queryName}` - Execute a query
- `POST /admin/query` - Create a new query
- `DELETE /admin/query/{queryName}` - Delete a query
- `GET /cache/{queryName}` - Execute a cached query

### Example Usage

```bash
# Add a query
curl -X POST -d "name=test_query&sql=SELECT 'Hello, http4k!' as message&description=Test" http://localhost:8002/admin/query

# Execute the query
curl http://localhost:8002/query/test_query

# Execute as cached query
curl http://localhost:8002/cache/test_query

# Delete the query
curl -X DELETE http://localhost:8002/admin/query/test_query
```

## Testing

Run the integration tests:

```bash
./gradlew :embers-http4k:test
```

## Architecture

The application is structured as follows:

- `Application.kt` - Main application entry point and HTTP routing
- `Http4kIntegrationTest.kt` - Integration tests

## Dependencies

- http4k-core: Lightweight HTTP toolkit
- http4k-server-jetty: Jetty server for http4k
- H2 Database: In-memory database for development
- JDBI: SQL convenience layer
- JUnit 5: Testing framework

## License

This project is licensed under the same license as the main Embers project.
