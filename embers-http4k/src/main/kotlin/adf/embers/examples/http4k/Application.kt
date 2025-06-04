package adf.embers.examples.http4k

import adf.embers.configuration.EmbersHandlerConfiguration
import adf.embers.configuration.EmbersProcessorConfiguration
import adf.embers.configuration.EmbersRepositoryConfiguration
import adf.embers.examples.http4k.adapter.toHttp4kResponse
import adf.embers.query.persistence.Query
import org.http4k.core.HttpHandler
import org.http4k.core.Method.*
import org.http4k.core.body.formAsMap
import org.http4k.core.getFirst
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.jdbi.v3.core.Jdbi
import javax.sql.DataSource

fun main() {
    // Initialize database and services
    val dataSource = createInMemoryH2DataSource()
    val jdbi = Jdbi.create(dataSource)

    // Create database tables
    createTables(jdbi)

    // Initialize Embers services using configuration
    val repositoryConfig = EmbersRepositoryConfiguration(dataSource)
    val processorConfig = EmbersProcessorConfiguration(repositoryConfig)
    val handlerConfig = EmbersHandlerConfiguration(processorConfig)

    // Create http4k app with the handler configuration
    val app = createHttp4kApp(handlerConfig)

    // Start server
    val server = app.startServer()
    println("Server started on http://localhost:${server.port()}")
}

fun createHttp4kApp(
    handlerConfig: EmbersHandlerConfiguration
): HttpHandler {
    val adminHandler = handlerConfig.adminQueryHandler()
    val queryHandler = handlerConfig.queryHandler()
    val queryResultCacheHandler = handlerConfig.queryResultCacheHandler()

    return ServerFilters.CatchAll().then(
        routes(
            // Admin endpoints
            "/admin/query" bind POST to { req ->
                val form = req.formAsMap()
                val jakartaResponse = adminHandler.addOrUpdateQuery(
                    Query(
                        form.getFirst("name"),
                        form.getFirst("description"),
                        form.getFirst("sql")
                    )
                )
                jakartaResponse.toHttp4kResponse()
            },

            "/admin/query/{queryName}" bind DELETE to { req ->
                val queryName = req.path("queryName")!!
                val jakartaResponse = adminHandler.deleteQuery(queryName)
                jakartaResponse.toHttp4kResponse()
            },

            // Query endpoints
            "/query/{queryName}" bind GET to { req ->
                val queryName = req.path("queryName")!!
                val jakartaResponse = queryHandler.executeQuery(queryName)
                jakartaResponse.toHttp4kResponse()
            },

            // Cache endpoints
            "/cache/{queryName}" bind GET to { req ->
                val queryName = req.path("queryName")!!

                val jakartaResponse = queryResultCacheHandler.executeQuery(queryName)
                jakartaResponse.toHttp4kResponse()
            })
    )
}

private fun HttpHandler.startServer(port: Int = 8002): Http4kServer {
    return this.asServer(Jetty(port)).start()
}

private fun createInMemoryH2DataSource(): DataSource {
    val dataSource = org.h2.jdbcx.JdbcDataSource()
    dataSource.setURL("jdbc:h2:mem:embers;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE")
    dataSource.user = "sa"
    dataSource.password = ""
    return dataSource
}

private fun createTables(jdbi: Jdbi) {
    jdbi.useHandle<Exception> { handle ->
        // Create queries table
        handle.execute(
            """
            CREATE TABLE IF NOT EXISTS queries (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description VARCHAR(255),
                sql CLOB NOT NULL,
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        )

        // Create query statistics table
        handle.execute(
            """
            CREATE TABLE IF NOT EXISTS query_statistics (
                id IDENTITY PRIMARY KEY,
                query_id BIGINT NOT NULL,
                execution_time BIGINT NOT NULL,
                execution_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (query_id) REFERENCES queries(id)
            )
        """.trimIndent()
        )

        // Create query result cache table
        handle.execute(
            """
            CREATE TABLE IF NOT EXISTS query_result_cache (
                id IDENTITY PRIMARY KEY,
                query_name VARCHAR(255) NOT NULL,
                query_parameters VARCHAR(4000) NOT NULL,
                result CLOB NOT NULL,
                cached_date TIMESTAMP NOT NULL,
                expiry_date TIMESTAMP NOT NULL,
                UNIQUE (query_name, query_parameters)
            )
        """.trimIndent()
        )
    }
}
