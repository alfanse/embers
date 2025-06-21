package adf.embers.examples.http4k

import adf.embers.configuration.EmbersHandlerConfiguration
import adf.embers.configuration.EmbersProcessorConfiguration
import adf.embers.configuration.EmbersRepositoryConfiguration
import adf.embers.examples.http4k.adapter.toHttp4kResponse
import adf.embers.query.persistence.Query
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.body.formAsMap
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.time.Duration.ofMillis
import javax.sql.DataSource

class EmbersHttp4k(
    private val dataSource: DataSource,
    private val port: Int = 8002
) {
    private lateinit var server: Http4kServer


    fun start() {
        // Initialize Embers services using configuration
        val repositoryConfig = EmbersRepositoryConfiguration(dataSource)
        val processorConfig = EmbersProcessorConfiguration(repositoryConfig)
        val handlerConfig = EmbersHandlerConfiguration(processorConfig)

        // Create and start the server
        val app = createHttp4kApp(handlerConfig)
        server = app.startServer(port)
        println("Server started on http://localhost:${server.port()}")
    }

    fun stop() {
        server.stop()
    }

    private fun createHttp4kApp(handlerConfig: EmbersHandlerConfiguration): HttpHandler {
        val adminHandler = handlerConfig.adminQueryHandler()
        val queryHandler = handlerConfig.queryHandler()
        val queryResultCacheHandler = handlerConfig.queryResultCacheHandler()

        return ServerFilters.CatchAll().then(
            routes(
                // Admin endpoints
                "/admin/query" bind POST to { req ->
                    handleRequest(req) {
                        val form = req.formAsMap()
                        adminHandler.addOrUpdateQuery(
                            Query(
                                form.getFirst("name"),
                                form.getFirst("description"),
                                form.getFirst("sql"),
                                form.getFirst("cacheableDuration")
                                    ?.toLongOrNull()
                                    ?.let { ofMillis(it) }
                            )
                        ).toHttp4kResponse()
                    }
                },

                "/admin/query/{queryName}" bind DELETE to { req ->
                    handleRequest(req) {
                        val queryName = req.path("queryName") ?: throw IllegalArgumentException("Query name is required")
                        adminHandler.deleteQuery(queryName).toHttp4kResponse()
                    }
                },

                // Query endpoints
                "/query/{queryName}" bind GET to { req ->
                    handleRequest(req) {
                        val queryName = req.path("queryName") ?: throw IllegalArgumentException("Query name is required")
                        queryHandler.executeQuery(queryName).toHttp4kResponse()
                    }
                },

                // Cache endpoints
                "/cache/{queryName}" bind GET to { req ->
                    handleRequest(req) {
                        val queryName = req.path("queryName") ?: throw IllegalArgumentException("Query name is required")
                        queryResultCacheHandler.executeQuery(queryName).toHttp4kResponse()
                    }
                }
            )
//                .then(
//                ServerFilters.RequestTracing.using(
//                    requestIdGenerator = { RequestId.generate() },
//                    before = { request, requestId ->
//                        MDC.put(MDC_KEY, requestId.value)
//                        logger.info("Starting request: ${request.method} ${request.uri}")
//                    },
//                    after = { request, response, requestId ->
//                        logger.info("Completed request: ${request.method} ${request.uri} with status ${response.status}")
//                        MDC.remove(MDC_KEY)
//                    },
//                    catchAll = { request, throwable, requestId ->
//                        logger.error("Error processing request: ${request.method} ${request.uri}", throwable)
//                        Response(INTERNAL_SERVER_ERROR).body(throwable.message ?: "Internal server error")
//                    }
//                ))
        )
    }


    private fun handleRequest(request: Request, handler: () -> Response): Response {
        return try {
            handler()
        } catch (e: Exception) {
            println("Error processing request: ${request.method} ${request.uri}, error: ${e.toString()}")
            Response(INTERNAL_SERVER_ERROR).body(e.message ?: "An error occurred")
        }
    }

    private fun HttpHandler.startServer(port: Int): Http4kServer {
        return this.asServer(Jetty(port)).start()
    }




}
