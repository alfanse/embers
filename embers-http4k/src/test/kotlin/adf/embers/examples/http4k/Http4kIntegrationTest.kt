package adf.embers.examples.http4k

import adf.embers.tools.EmbersDatabase
import org.assertj.core.api.Assertions.assertThat
import org.http4k.client.JavaHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.body.form
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private const val QUERY_NAME = "test_query"

class Http4kIntegrationTest {
    companion object {
        private val client = JavaHttpClient()
        private const val BASE_URL = "http://localhost:8002"
        private lateinit var embersDb: EmbersDatabase

        @JvmStatic
        @BeforeAll
        fun setup() {
            // Initialize the database
            embersDb = EmbersDatabase("jdbc:hsqldb:mem:testdb;")
            embersDb.startInMemoryDatabase()
            embersDb.createEmbersDdl()
            
            // Start the application with the test database
            startApplication(embersDb.dataSource)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            embersDb.shutdownInMemoryDatabase()
        }
    }

    @Test
    fun `test query lifecycle`() {
        // Add a query
        val addResponse = client(
            Request(Method.POST, "$BASE_URL/admin/query")
                .form("name", QUERY_NAME)
                .form("sql", "SELECT 'Hello, http4k!' as message")
                .form("description", "Test query for http4k integration")
        )
        
        assertThat(addResponse.status.code).isEqualTo(200)
        
        // Execute the query
        val queryResponse = client(
            Request(Method.GET, "$BASE_URL/query/test_query")
        )
        
        assertThat(queryResponse.status.code).isEqualTo(200)
        assertThat(queryResponse.bodyString()).contains("Hello, http4k!")
        
        // Test cached query
        val cachedResponse = client(
            Request(Method.GET, "$BASE_URL/cache/test_query")
        )
        
        assertThat(cachedResponse.status.code).isEqualTo(200)
        assertThat(cachedResponse.bodyString()).contains("Hello, http4k!")
        
        // Delete the query
        val deleteResponse = client(
            Request(Method.DELETE, "$BASE_URL/admin/query/test_query")
        )
        
        assertThat(deleteResponse.status.code).isEqualTo(200)
        
        // Verify query is deleted
        val notFoundResponse = client(
            Request(Method.GET, "$BASE_URL/query/test_query")
        )
        
        assertThat(notFoundResponse.status.code).isEqualTo(400)
    }
}
