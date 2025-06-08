package adf.embers.examples.http4k

import javax.sql.DataSource

fun main() {
    // Create and initialize the database
    val dbInitializer = DatabaseInitializer()
    val dataSource = dbInitializer.initializeInMemoryH2()
    
    // Start the application with the initialized data source
    startApplication(dataSource)
}

/**
 * Starts the application with the provided data source.
 * This is separated from main() to allow for testing with different data sources.
 */
fun startApplication(dataSource: DataSource) {
    try {
        // Start the HTTP server
        val server = EmbersHttp4k(dataSource)
        server.start()
        
        // Add shutdown hook to clean up resources
        Runtime.getRuntime().addShutdownHook(Thread {
            server.stop()
        })
        
        // Keep the main thread alive
//        Thread.currentThread().join()
    } catch (e: Exception) {
        System.err.println("Error starting application: ${e.message}")
        e.printStackTrace()
        throw e
    }
}
