package adf.embers.examples.http4k

import adf.embers.admin.AdminQueryHandler
import adf.embers.cache.QueryResultCacheHandler
import adf.embers.query.QueryHandler
import org.eclipse.jetty.server.Server
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import javax.sql.DataSource

fun main() {
    // Initialize database and services
    val dataSource = createInMemoryH2DataSource()
    val jdbi = Jdbi.create(dataSource)
    
    // Create database tables
    createTables(jdbi)
    
    // Initialize Embers services
    val queryHandler = QueryHandler(dataSource, jdbi.onDemand())
    val adminHandler = AdminQueryHandler(jdbi.onDemand())
    val cacheHandler = QueryResultCacheHandler(jdbi.onDemand())
    
    // Create http4k app
    val app = createHttp4kApp(queryHandler, adminHandler, cacheHandler)
    
    // Start server
    val server = app.startServer()
    println("Server started on http://localhost:${server.port()}")
}

fun createHttp4kApp(
    queryHandler: QueryHandler,
    adminHandler: AdminQueryHandler,
    cacheHandler: QueryResultCacheHandler
): HttpHandler {
    val json = org.http4k.format.Jackson
    
    return ServerFilters.CatchAll()
        .then(routes(
            // Query endpoints
            "/query/{queryName}" bind GET to { req ->
                val queryName = req.path("queryName")!!
                val params = req.queryMap()
                
                val result = queryHandler.runQuery(queryName, params)
                
                if (result.hasErrors()) {
                    Response(Status.BAD_REQUEST)
                        .body(json.writeValueAsString(mapOf("errors" to result.errors)))
                } else {
                    Response(OK)
                        .header("Content-Type", "application/json")
                        .body(result.result)
                }
            },
            
            // Admin endpoints
            "/admin/query" bind POST to { req ->
                val form = req.form("name", "sql", "description")
                val result = adminHandler.addQuery(form)
                Response(OK).body(result)
            },
            
            "/admin/query/{queryName}" bind DELETE to { req ->
                val queryName = req.path("queryName")!!
                val result = adminHandler.deleteQuery(queryName)
                Response(OK).body(result)
            },
            
            // Cache endpoints
            "/cache/{queryName}" bind GET to { req ->
                val queryName = req.path("queryName")!!
                val params = req.queryMap()
                
                val result = cacheHandler.runCachedQuery(queryName, params)
                
                if (result.hasErrors()) {
                    Response(Status.BAD_REQUEST)
                        .body(json.writeValueAsString(mapOf("errors" to result.errors)))
                } else {
                    Response(OK)
                        .header("Content-Type", "application/json")
                        .body(result.result)
                }
            }
        ))
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
        handle.execute("""
            CREATE TABLE IF NOT EXISTS queries (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description VARCHAR(255),
                sql CLOB NOT NULL,
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent())
        
        // Create query statistics table
        handle.execute("""
            CREATE TABLE IF NOT EXISTS query_statistics (
                id IDENTITY PRIMARY KEY,
                query_id BIGINT NOT NULL,
                execution_time BIGINT NOT NULL,
                execution_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (query_id) REFERENCES queries(id)
            )
        """.trimIndent())
        
        // Create query result cache table
        handle.execute("""
            CREATE TABLE IF NOT EXISTS query_result_cache (
                id IDENTITY PRIMARY KEY,
                query_name VARCHAR(255) NOT NULL,
                query_parameters VARCHAR(4000) NOT NULL,
                result CLOB NOT NULL,
                cached_date TIMESTAMP NOT NULL,
                expiry_date TIMESTAMP NOT NULL,
                UNIQUE (query_name, query_parameters)
            )
        """.trimIndent())
    }
}
